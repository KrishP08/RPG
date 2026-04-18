package org.rpg.client.ui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.rpg.client.network.RpgNetworkClient;
import org.rpg.network.RpgNetwork;
import org.rpg.stats.ClientStatsCache;
import org.rpg.stats.PlayerStats;
import org.rpg.stats.*;

public class SkillTreeScreen extends Screen {
    private static final int COL_W=110;
    private static final int COL_PAD=20;
    private static final int SKILL_H=32;
    private static final int HEADER_H=50;
    private static final int TOP_PAD=30;

    private static final String[][][] SKILL_INFO={
            {//STRENGTH
                    {"Power Strike","+20% melee Damage"},
                    {"Berserker Rang","Bonus Damage at low HP"},
                    {"Titan","Double attack range"},
            },
            {//AGILITY
                    {"Swift Feet","+15% move speed"},
                    {"Evasion","+10% Dodge chance"},
                    {"Phantom","Double Jump Height"},
            },
            {//ENDURANCE
                    {"Iron Skin","+4 armor Points"},
                    {"Regeneration","Regen 1HP/10s"},
                    {"Immortal","SUrvive 1 lethal Hit"},
            },
            {//MINING
                    {"Vein Miner","Break ore veins at once"},
                    {"Prospector","Show ores through Walls"},
                    {"Gold Rush","Double ore Drops"},
            },
            {//MAGIC
                    {"Arcane Boost","+30% XP From kills"},
                    {"Enchant Master","Better enchant rolls"},
                    {"Spell Bind","Teleport on sneak+jump"},
            }
    };
    private static final int[] STAT_COLORS={
            0xFFFF5555,//RED STR
            0xff55ff55,//GREEN AGILI
            0xFF5588FF,//BLUE ENDU
            0XFFFFAA33,//ORANGE MINING
            0XFFCC55FF,//PURPLE MAGIC
    };
    private static final StatType[] STAT_TYPES={
            StatType.STRENGTH,StatType.AGILITY,StatType.ENDURANCE,StatType.MINING,StatType.MAGIC
    };
    private String tooltipName=null;
    private String tooltipDesc=null;

    public SkillTreeScreen(){
        super(Text.literal("Skill Tree"));
    }

    @Override
    public boolean shouldPause(){return false;}

    public void render(DrawContext context, int mouseX, int mouseY, float delta){
        context.fill(0,0,width,height,0xCC000000);

        PlayerStats stats= ClientStatsCache.getStats();
        int totalCols=5;
        int totalW =totalCols*COL_W+(totalCols-1)*COL_PAD;
        int startX=(width-totalW)/2;
        int startY=TOP_PAD;

        tooltipName=null;
        tooltipDesc=null;

        context.drawCenteredTextWithShadow(textRenderer,Text.literal("✦ SKILL TREE ✦"),width/2,10,0xFFFFDD44);

        context.drawCenteredTextWithShadow(textRenderer,Text.literal("Skill Points: "+stats.skillPoints),width/2,22,0xFFFFFFAA);

        for(int i=0;i<5;i++){
            int colX=startX+i*(COL_W+COL_PAD);
            drawStatColumn(context, colX, startY, i, stats, mouseX, mouseY);
        }
        if(tooltipName!=null){
            int tw=Math.max(textRenderer.getWidth(tooltipName),
                    textRenderer.getWidth(tooltipDesc))+12;
            int tx =Math.min(mouseX+8,width-tw-4);
            int ty=mouseY-30;
            context.fill(tx,ty,tx+tw,ty+28,0xFF222222);
            context.fill(tx,ty,tx+tw,ty+1,0xFFFFDD44);
            context.drawTextWithShadow(textRenderer,Text.literal(tooltipName),tx+6,ty+6,0xFFFFDD44);
            context.drawTextWithShadow(textRenderer,Text.literal(tooltipDesc),tx+6,ty+17,0xFFCCCCCC);
        }
        context.drawCenteredTextWithShadow(textRenderer,Text.literal("Press K or ESPto Close"),width/2,height-14,0xFF888888);
    }
    private void drawStatColumn(DrawContext ctx,int x,int y,int statIdx,PlayerStats stats,int mouseX,int mouseY){
        StatType type=STAT_TYPES[statIdx];
        int color=STAT_COLORS[statIdx];
        int level=getLevel(stats,statIdx);
        int xp=getXp(stats,statIdx);
        int needed=PlayerStats.xpForLevel(level);
        int skillMask=getSkillMask(stats,statIdx);

        ctx.fill(x,y,x+COL_W,y+HEADER_H,0xFF1A1A1A);
        ctx.fill(x,y,x+COL_W,y+2,color);

        ctx.drawCenteredTextWithShadow(textRenderer,Text.literal(type.getDisplayName()),x+COL_W/2,y+6,color);

        ctx.drawCenteredTextWithShadow(textRenderer,Text.literal("Level "+level),x+COL_W/2,y+17,0xFFFFFFFF);

        int barX=x+8;
        int barY=y+30;
        int barW=COL_W-16;
        ctx.fill(barX,barY,barX+barW,barY+5,0xFF333333);
        float pct=Math.min(1f,(float)xp/needed);
        int fill=(int)(barW*pct);
        if (fill>0) ctx.fill(barX,barY,barX+barW,barY+5,color);

        ctx.drawCenteredTextWithShadow(textRenderer,Text.literal(xp+"/"+needed+"XP"),x+COL_W/2,y+38,0xFFAAAAAA);

        for(int s=0;s<3;s++){
            int skillY=y+HEADER_H+10+s*(SKILL_H+8);
            drawSkillNode(ctx,x,skillY,statIdx,s,skillMask,stats,level,color,mouseX,mouseY);
        }
    }
    private void drawSkillNode(DrawContext ctx,int x,int y,int statIdx,int skillIdx,int skillMask,PlayerStats stats,int level,int color,int mouseX,int mouseY){
            int requiredLevel=(skillIdx+1)*10;
            boolean unlocked =(skillMask & (1<<skillIdx))!=0;
            boolean canUnlock=!unlocked && level >=requiredLevel && stats.skillPoints>0;
            boolean hovered=mouseX>=x && mouseX<=x+COL_W && mouseY>=y && mouseY<=y+SKILL_H;
            String name =SKILL_INFO[statIdx][skillIdx][0];
            String desc =SKILL_INFO[statIdx][skillIdx][1];

            int bgColor=unlocked ? 0xFF1A3A1A : (canUnlock ? 0xFF2A2A2A : 0xFF111111);
            ctx.fill(x+4,y,x+COL_W-4,y+SKILL_H,bgColor);

            if(hovered && (unlocked || canUnlock)){
                ctx.fill(x+4,y,x+COL_W-4,y+SKILL_H,0x33FFFFFF);
                tooltipName=name;
                tooltipDesc=desc;
            }
            int boederColor=unlocked ? color : (canUnlock ? 0xFF666666 : 0xFF333333);

            ctx.fill(x+4,y,x+COL_W-4,y+1,boederColor);
            ctx.fill(x+4,y+SKILL_H-1,x+COL_W-4,y+SKILL_H,boederColor);

            String icon =unlocked ? "✔" : (canUnlock ? "○" : "✕");
            int iconColor=unlocked ? 0xFF55FF55 :(canUnlock ? 0xFFFFFF55 : 0xFF555555);
            ctx.drawTextWithShadow(textRenderer,Text.literal(icon),x+8,y+5,iconColor);

            int nameColor=unlocked ? 0xFFFFFFFF : (canUnlock ? 0xFFDDDDDD : 0xFF666666);
            ctx.drawTextWithShadow(textRenderer,Text.literal(name),x+20,y+5,nameColor);

            String reqText="Req: Lv."+requiredLevel;
            int reqColor=level>=requiredLevel ? 0xFF88FF88 : 0xFFFF6666;
            ctx.drawTextWithShadow(textRenderer,Text.literal(reqText),x+20,y+16,reqColor);

            if(skillIdx<2){
                int lineX=x+COL_W/2;
                ctx.fill(lineX,y+SKILL_H,lineX+1,y+SKILL_H+8,0xFF444444);
            }
    }
    @Override
    public boolean mouseClicked(double mouseX,double mouseY,int button){
        if(button !=0) return false;

        PlayerStats stats=ClientStatsCache.getStats();
        int totalCols=5;
        int totalW=totalCols+COL_W+(totalCols-1)*COL_PAD;
        int startX=(width-totalW)/2;
        int startY=TOP_PAD;

        for (int i=0;i<5;i++){
            int colX=startX+i*(COL_W+COL_PAD);
            for (int s=0;s<3;s++){
                int skillY=startY+HEADER_H+10+s*(SKILL_H+8);
                if(mouseX>=colX+4&&mouseX<=colX+COL_W-4&&mouseY>=skillY&&mouseY<=skillY+SKILL_H){
                    RpgNetworkClient.sendUnlockSkill(STAT_TYPES[i],s);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode,int scanCode,int modifiers){
        if(keyCode==75||keyCode==256){
            this.close();
            return true;
        }
        return super.keyPressed(keyCode,scanCode,modifiers);
    }

    private int getLevel(PlayerStats s,int idx){
        return switch (idx){
            case 0-> s.strengthLevel;
            case 1-> s.agilityLevel;
            case 2-> s.enduranceLevel;
            case 3-> s.miningLevel;
            case 4-> s.magicLevel;
            default -> 1;
        };
    }

    private int getXp(PlayerStats s,int idx){
        return switch (idx){
            case 0-> s.strengthXp;
            case 1-> s.agilityXp;
            case 2-> s.enduranceXp;
            case 3-> s.miningXp;
            case 4-> s.magicXp;
            default -> 0;
        };
    }

    private int getSkillMask(PlayerStats s,int idx){
        return switch (idx){
            case 0-> s.strengthSkills;
            case 1-> s.agilitySkills;
            case 2-> s.enduranceSkills;
            case 3-> s.miningSkills;
            case 4-> s.magicSkills;
            default -> 0;
        };
    }
}
