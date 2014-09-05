package com.renderscript.launcher.widget;

import android.content.res.Resources;
import android.renderscript.Allocation;
import android.renderscript.BaseObj;
import android.renderscript.FieldPacker;
//import android.renderscript.ProgramFragment;
//import android.renderscript.ProgramStore;
import android.renderscript.RenderScript;
import android.renderscript.ScriptC;

public class ScriptC_preview extends ScriptC {
    private FieldPacker __rs_fp_ALLOCATION;
    private FieldPacker __rs_fp_F32;
    private FieldPacker __rs_fp_I32;
    private FieldPacker __rs_fp_PROGRAM_FRAGMENT;
    private FieldPacker __rs_fp_PROGRAM_STORE;
    private static final String __rs_resource_name = "preview";
    private static final int mExportFuncIdx_initPTextures = 5;
    private static final int mExportFuncIdx_initPreview = 4;
    private static final int mExportFuncIdx_onClick = 0;
    private static final int mExportFuncIdx_onDeleteScreen = 2;
    private static final int mExportFuncIdx_onLongPress = 1;
    private static final int mExportFuncIdx_onTouchEvent = 3;
    private static final int mExportVarIdx_gPFSLeaf = 1;
    private static final int mExportVarIdx_gPFSPage = 2;
    private static final int mExportVarIdx_gPFTexture = 0;
    private static final int mExportVarIdx_gPFTexture565 = 3;
    private static final int mExportVarIdx_mAddItem = 17;
    private static final int mExportVarIdx_mBigAllocation = 18;
    private static final int mExportVarIdx_mBigIndex = 19;
    private static final int mExportVarIdx_mBlankPage = 15;
    private static final int mExportVarIdx_mCurrentItem = 21;
    private static final int mExportVarIdx_mDefaultPage = 16;
    private static final int mExportVarIdx_mDefaultPageIndex = 27;
    private static final int mExportVarIdx_mEndSizeY = 30;
    private static final int mExportVarIdx_mHeight = 23;
    private static final int mExportVarIdx_mIsDraw = 26;
    private static final int mExportVarIdx_mOffset = 25;
    private static final int mExportVarIdx_mPageCount = 24;
    private static final int mExportVarIdx_mStartSizeY = 29;
    private static final int mExportVarIdx_mState = 20;
    private static final int mExportVarIdx_mTargetItem = 28;
    private static final int mExportVarIdx_mTexture_Page1 = 4;
    private static final int mExportVarIdx_mTexture_Page2 = 5;
    private static final int mExportVarIdx_mTexture_Page3 = 6;
    private static final int mExportVarIdx_mTexture_Page4 = 7;
    private static final int mExportVarIdx_mTexture_Page5 = 8;
    private static final int mExportVarIdx_mTexture_Page6 = 9;
    private static final int mExportVarIdx_mTexture_Page7 = 10;
    private static final int mExportVarIdx_mTexture_Page8 = 11;
    private static final int mExportVarIdx_mTexture_Page9 = 12;
    private static final int mExportVarIdx_mTrashPressOFFTex = 14;
    private static final int mExportVarIdx_mTrashPressONTex = 13;
    private static final int mExportVarIdx_mWidth = 22;
   /* private ProgramStore mExportVar_gPFSLeaf;
    private ProgramStore mExportVar_gPFSPage;
    private ProgramFragment mExportVar_gPFTexture;
    private ProgramFragment mExportVar_gPFTexture565;*/
    private Allocation mExportVar_mAddItem;
    private Allocation mExportVar_mBigAllocation;
    private int mExportVar_mBigIndex;
    private Allocation mExportVar_mBlankPage;
    private int mExportVar_mCurrentItem;
    private Allocation mExportVar_mDefaultPage;
    private int mExportVar_mDefaultPageIndex;
    private float mExportVar_mEndSizeY;
    private int mExportVar_mHeight;
    private int mExportVar_mIsDraw;
    private float mExportVar_mOffset;
    private int mExportVar_mPageCount;
    private float mExportVar_mStartSizeY;
    private int mExportVar_mState;
    private int mExportVar_mTargetItem;
    private Allocation mExportVar_mTexture_Page1;
    private Allocation mExportVar_mTexture_Page2;
    private Allocation mExportVar_mTexture_Page3;
    private Allocation mExportVar_mTexture_Page4;
    private Allocation mExportVar_mTexture_Page5;
    private Allocation mExportVar_mTexture_Page6;
    private Allocation mExportVar_mTexture_Page7;
    private Allocation mExportVar_mTexture_Page8;
    private Allocation mExportVar_mTexture_Page9;
    private Allocation mExportVar_mTrashPressOFFTex;
    private Allocation mExportVar_mTrashPressONTex;
    private int mExportVar_mWidth;

    public ScriptC_preview(RenderScript rs, Resources resources, int id) {
        super(rs, resources, id);
        this.mExportVar_mCurrentItem = 0;
        this.mExportVar_mOffset = 0f;
        this.mExportVar_mIsDraw = 0;
        this.mExportVar_mEndSizeY = -1f;
    }

    public ScriptC_preview(RenderScript rs) {
        this(rs, rs.getApplicationContext().getResources(), rs.getApplicationContext().getResources()
                .getIdentifier("preview", "raw", rs.getApplicationContext().getPackageName()));
    }

  /*  public ProgramStore get_gPFSLeaf() {
        return this.mExportVar_gPFSLeaf;
    }

    public ProgramStore get_gPFSPage() {
        return this.mExportVar_gPFSPage;
    }

    public ProgramFragment get_gPFTexture() {
        return this.mExportVar_gPFTexture;
    }

    public ProgramFragment get_gPFTexture565() {
        return this.mExportVar_gPFTexture565;
    }*/

    public Allocation get_mAddItem() {
        return this.mExportVar_mAddItem;
    }

    public Allocation get_mBigAllocation() {
        return this.mExportVar_mBigAllocation;
    }

    public int get_mBigIndex() {
        return this.mExportVar_mBigIndex;
    }

    public Allocation get_mBlankPage() {
        return this.mExportVar_mBlankPage;
    }

    public int get_mCurrentItem() {
        return this.mExportVar_mCurrentItem;
    }

    public Allocation get_mDefaultPage() {
        return this.mExportVar_mDefaultPage;
    }

    public int get_mDefaultPageIndex() {
        return this.mExportVar_mDefaultPageIndex;
    }

    public float get_mEndSizeY() {
        return this.mExportVar_mEndSizeY;
    }

    public int get_mHeight() {
        return this.mExportVar_mHeight;
    }

    public int get_mIsDraw() {
        return this.mExportVar_mIsDraw;
    }

    public float get_mOffset() {
        return this.mExportVar_mOffset;
    }

    public int get_mPageCount() {
        return this.mExportVar_mPageCount;
    }

    public float get_mStartSizeY() {
        return this.mExportVar_mStartSizeY;
    }

    public int get_mState() {
        return this.mExportVar_mState;
    }

    public int get_mTargetItem() {
        return this.mExportVar_mTargetItem;
    }

    public Allocation get_mTexture_Page1() {
        return this.mExportVar_mTexture_Page1;
    }

    public Allocation get_mTexture_Page2() {
        return this.mExportVar_mTexture_Page2;
    }

    public Allocation get_mTexture_Page3() {
        return this.mExportVar_mTexture_Page3;
    }

    public Allocation get_mTexture_Page4() {
        return this.mExportVar_mTexture_Page4;
    }

    public Allocation get_mTexture_Page5() {
        return this.mExportVar_mTexture_Page5;
    }

    public Allocation get_mTexture_Page6() {
        return this.mExportVar_mTexture_Page6;
    }

    public Allocation get_mTexture_Page7() {
        return this.mExportVar_mTexture_Page7;
    }

    public Allocation get_mTexture_Page8() {
        return this.mExportVar_mTexture_Page8;
    }

    public Allocation get_mTexture_Page9() {
        return this.mExportVar_mTexture_Page9;
    }

    public Allocation get_mTrashPressOFFTex() {
        return this.mExportVar_mTrashPressOFFTex;
    }

    public Allocation get_mTrashPressONTex() {
        return this.mExportVar_mTrashPressONTex;
    }

    public int get_mWidth() {
        return this.mExportVar_mWidth;
    }

    public void invoke_initPTextures() {
        this.invoke(5);
    }

    public void invoke_initPreview() {
        this.invoke(4);
    }

    public void invoke_onClick() {
        this.invoke(0);
    }

    public void invoke_onDeleteScreen() {
        this.invoke(2);
    }

    public void invoke_onLongPress() {
        this.invoke(1);
    }

    public void invoke_onTouchEvent(int action, int x, int y) {
        FieldPacker v0 = new FieldPacker(12);
        v0.addI32(action);
        v0.addI32(x);
        v0.addI32(y);
        this.invoke(3, v0);
    }

    /*public synchronized void set_gPFSLeaf(ProgramStore v) {
        this.setVar(1, (v));
        this.mExportVar_gPFSLeaf = v;
    }

    public synchronized void set_gPFSPage(ProgramStore v) {
        this.setVar(2, (v));
        this.mExportVar_gPFSPage = v;
    }

    public synchronized void set_gPFTexture(ProgramFragment v) {
        this.setVar(0, (v));
        this.mExportVar_gPFTexture = v;
    }

    public synchronized void set_gPFTexture565(ProgramFragment v) {
        int v0 = 3;
        this.setVar(v0, (v));
        this.mExportVar_gPFTexture565 = v;
    }*/

    public synchronized void set_mAddItem(Allocation v) {
        this.setVar(17, (v));
        this.mExportVar_mAddItem = v;
    }

    public synchronized void set_mBigAllocation(Allocation v) {
        this.setVar(18, (v));
        this.mExportVar_mBigAllocation = v;
    }

    public synchronized void set_mBigIndex(int v) {
        this.setVar(19, v);
        this.mExportVar_mBigIndex = v;
    }

    public synchronized void set_mBlankPage(Allocation v) {
        this.setVar(15, (v));
        this.mExportVar_mBlankPage = v;
    }

    public synchronized void set_mCurrentItem(int v) {
        this.setVar(21, v);
        this.mExportVar_mCurrentItem = v;
    }

    public synchronized void set_mDefaultPage(Allocation v) {
        this.setVar(16, (v));
        this.mExportVar_mDefaultPage = v;
    }

    public synchronized void set_mDefaultPageIndex(int v) {
        this.setVar(27, v);
        this.mExportVar_mDefaultPageIndex = v;
    }

    public synchronized void set_mEndSizeY(float v) {
        this.setVar(30, v);
        this.mExportVar_mEndSizeY = v;
    }

    public synchronized void set_mHeight(int v) {
        this.setVar(23, v);
        this.mExportVar_mHeight = v;
    }

    public void set_mIsDraw(int v) {
        this.setVar(26, v);
        this.mExportVar_mIsDraw = v;
    }

    public void set_mOffset(float v) {
        this.setVar(25, v);
        this.mExportVar_mOffset = v;
    }

    public void set_mPageCount(int v) {
        this.setVar(24, v);
        this.mExportVar_mPageCount = v;
    }

    public void set_mStartSizeY(float v) {
        this.setVar(29, v);
        this.mExportVar_mStartSizeY = v;
    }

    public void set_mState(int v) {
        this.setVar(20, v);
        this.mExportVar_mState = v;
    }

    public void set_mTargetItem(int v) {
        this.setVar(28, v);
        this.mExportVar_mTargetItem = v;
    }

    public void set_mTexture_Page1(Allocation v) {
        this.setVar(4, (v));
        this.mExportVar_mTexture_Page1 = v;
    }

    public void set_mTexture_Page2(Allocation v) {
        this.setVar(5, (v));
        this.mExportVar_mTexture_Page2 = v;
    }

    public void set_mTexture_Page3(Allocation v) {
        this.setVar(6, (v));
        this.mExportVar_mTexture_Page3 = v;
    }

    public void set_mTexture_Page4(Allocation v) {
        this.setVar(7, (v));
        this.mExportVar_mTexture_Page4 = v;
    }

    public void set_mTexture_Page5(Allocation v) {
        this.setVar(8, (v));
        this.mExportVar_mTexture_Page5 = v;
    }

    public void set_mTexture_Page6(Allocation v) {
        this.setVar(9, (v));
        this.mExportVar_mTexture_Page6 = v;
    }

    public void set_mTexture_Page7(Allocation v) {
        this.setVar(10, (v));
        this.mExportVar_mTexture_Page7 = v;
    }

    public void set_mTexture_Page8(Allocation v) {
        this.setVar(11, (v));
        this.mExportVar_mTexture_Page8 = v;
    }

    public void set_mTexture_Page9(Allocation v) {
        this.setVar(12, (v));
        this.mExportVar_mTexture_Page9 = v;
    }

    public void set_mTrashPressOFFTex(Allocation v) {
        this.setVar(14, (v));
        this.mExportVar_mTrashPressOFFTex = v;
    }

    public void set_mTrashPressONTex(Allocation v) {
        this.setVar(13, (v));
        this.mExportVar_mTrashPressONTex = v;
    }

    public void set_mWidth(int v) {
        this.setVar(22, v);
        this.mExportVar_mWidth = v;
    }
}
