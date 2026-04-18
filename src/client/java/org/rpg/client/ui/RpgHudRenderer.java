package org.rpg.client.ui;

import org.rpg.stats.ClientStatsCache;
import org.rpg.stats.PlayerStats;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class RpgHudRenderer {
    private static final int PANEL_W=148;
    private static final int PANEL_PAD=6;
    private static final int ROW_H=12;
    private static final int BAR_W=52;
    private static final int BAR_H=4;

    private static final int C_PANEL_BG   = 0xBB0A0A0F;
    private static final int C_HEADER_BG  = 0xCC141420;
    private static final int C_DIVIDER    = 0xFF2A2A44;
    private static final int C_BAR_TRACK  = 0xFF1C1C2E;
    private static final int C_TITLE      = 0xFFCCCCFF;
    private static final int C_HINT       = 0xFF555577;
    private static final int C_POINTS     = 0xFFFFDD44;
    private static final int C_LVL_TEXT   = 0xFFEEEEEE;

    private static final int[] STAT_COLORS={
           0xFFFF5555,//ST-CRIMSON
           0xFF44FF88,//AGI-MINT
           0xFF5599FF,//END-SKYBLUE
           0xFFFFAA22,//MIN-AMBER
           0xFFCC55FF,//MAG-VIOLET
    };

    private static final String[] STAT_ICON={"\u2694","\u25c8","\u2764","\u26cf","\u2726"};
    private static final String[] STAT_LABELS={"STR","AGI","END","MIN","MAG"};

    private static long lastBlink =0;
    private static boolean blinkOn=true;

    public static void render(DrawContext ctx){
        MinecraftClient mc = MinecraftClient.getInstance();
        if(mc.player == null ||mc.options.hudHidden) return;
        if (mc.currentScreen!=null) return;

        TextRenderer font=mc.textRenderer;
        PlayerStats s = ClientStatsCache.getStats();

        int screenW=mc.getWindow().getScaledWidth();
        int rows=5;
        int headerH=14;
        int footerH=s.skillPoints>0?14:0;
        int panelH =PANEL_PAD+headerH+2+rows*ROW_H+(footerH>0?2+footerH:0)+PANEL_PAD;
        int panelX=screenW-PANEL_W-4;
        int panelY=4;

        //Shadow
        ctx.fill(panelX+2,panelY+2,panelX+PANEL_W+2,panelY+panelH+2,0x44000000);
        //Panel BG
        ctx.fill(panelX, panelY, panelX + PANEL_W, panelY + panelH, C_PANEL_BG);
        // Left accent stripe
        ctx.fill(panelX, panelY, panelX + 2, panelY + panelH, 0xFF5555CC);
        // Top border
        ctx.fill(panelX, panelY, panelX + PANEL_W, panelY + 1, 0xFF5555CC);

        int hY=panelH+PANEL_PAD;
        ctx.fill(panelX+2,hY-2,panelX+PANEL_W,hY+headerH-1,C_HEADER_BG);
        ctx.drawTextWithShadow(font,Text.literal("RPG STATS"),panelX+PANEL_PAD+2,hY,C_TITLE);
        ctx.drawTextWithShadow(font,Text.literal("[K]"),panelX+PANEL_W-font.getWidth("[K]")-PANEL_PAD,hY,C_HINT);

        int divY=hY+headerH;
        ctx.fill(panelX+2,divY,panelX+PANEL_W,divY+1,C_DIVIDER);

        int rowY=divY+3;
        int[] levels={
                s.strengthLevel,s.agilityLevel,s.enduranceLevel,s.miningLevel,s.magicLevel};
        int[] xps={s.strengthXp, s.agilityXp,s.enduranceXp,s.miningXp,s.magicXp};

        for (int i=0;i<5;i++){
            drawStatRow(ctx,font,panelX+PANEL_PAD+2,rowY+i*ROW_H,i,levels[i],xps[i]);
        }
        if (s.skillPoints>0){
            int fDivY=rowY+rows*ROW_H+1;
            ctx.fill(panelX+2,fDivY,panelX+PANEL_W,fDivY+1,C_DIVIDER);
            long now=System.currentTimeMillis();
            if(now-lastBlink>600){
                blinkOn=!blinkOn;lastBlink=now;
            }
            int ptColor=blinkOn ? C_POINTS : 0xFFAA8800;

            String ptText =">> "+s.skillPoints+" Skill Point"+(s.skillPoints>1?"s":"")+" Ready!";
            ctx.drawTextWithShadow(font,Text.literal(ptText),panelX+PANEL_PAD+2,fDivY+4,ptColor);
        }
    }

    private static void drawStatRow(DrawContext ctx,TextRenderer font,int x,int y,int statIdx,int level,int xp){
        int color=STAT_COLORS[statIdx];
        String icon=STAT_ICON[statIdx];
        String label=STAT_LABELS[statIdx];

        if(statIdx%2==0){
            ctx.fill(x-2,y-1,x+PANEL_W-6,y+ROW_H-2,0x11FFFFFF);
        }
        ctx.drawTextWithShadow(font,Text.literal(icon),x,y,color);
        ctx.drawTextWithShadow(font,Text.literal(label),x+10,y,C_LVL_TEXT);

        String lvlStr=String.valueOf(level);
        ctx.drawTextWithShadow(font,Text.literal(lvlStr),x+35-font.getWidth(lvlStr),y,color);

        int barX=x+38;
        int barY=y+(ROW_H/2)-(BAR_H/2);
        ctx.fill(barX,barY,barX+BAR_W,barY+BAR_H,C_BAR_TRACK);
        int needed=PlayerStats.xpForLevel(level);
        float pct =Math.min(1f,(float) xp/needed);
        int fill=(int)(BAR_W*pct);
        if (fill>0){
            ctx.fill(barX,barY,barX+fill,barY+BAR_H,color);
            ctx.fill(barX,barY,barX+fill,barY+1,brighten(color));
        }
        if (level>=PlayerStats.MAX_LEVEL){
            ctx.drawTextWithShadow(font,Text.literal("MAX"),barX+16,barY-1,0xFFFFDD44);
        }
    }

    private static int brighten(int color){
        int r=Math.min(255,((color>>16)&0xFF)+0x30);
        int g=Math.min(255,((color>>8)&0xFF)+0x30);
        int b=Math.min(255,(color&0xFF)+0x30);
        return (0xFF<<24)|(r<<16)|(g<<8)|b;
    }
}
