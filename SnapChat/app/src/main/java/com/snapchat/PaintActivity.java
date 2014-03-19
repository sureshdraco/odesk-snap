package com.snapchat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerOpenListener;

import cn.Ragnarok.BitmapFilter;

public class PaintActivity extends Activity {

    String path;
    ImageView image;
    int width, height;
    DisplayMetrics metrixs = new DisplayMetrics();
    LayoutParams rParams;
    Bitmap workingBitmap;
    DrawPanel panel;
    ImageButton delete, next;
    RelativeLayout editing, panel_layout;
    RelativeLayout main;
    File output;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paint);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            path = extras.getString("filepath");
            Log.d("paint", "" + path);
        }

        getWindowManager().getDefaultDisplay().getMetrics(metrixs);
        width = metrixs.widthPixels;
        height = metrixs.heightPixels;

        editing = (RelativeLayout) findViewById(R.id.paint_editing_layout);
        panel_layout = (RelativeLayout) findViewById(R.id.paint_panel_layout);

        image = (ImageView) findViewById(R.id.paint_image);
        workingBitmap = BitmapFactory.decodeFile(path);
        image.setImageBitmap(workingBitmap);

        panel = new DrawPanel(getApplicationContext(), PaintActivity.this);
        rParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        rParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        main = (RelativeLayout) findViewById(R.id.paint_main);
        panel_layout.addView(panel, rParams);

//		panel.setVisibility(View.INVISIBLE);

        /**
         * colors Sliding Drawer
         */
        rParams = new LayoutParams((int) (width * 0.2575) + (int) (width * 0.075) + (int) (width * 0.02), (int) (height * 0.63));
        rParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        rParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        rParams.topMargin = (int) (height * 0.1);
        ((SlidingDrawer) findViewById(R.id.SlidingDrawer_colors)).setLayoutParams(rParams);


        ((ImageView) findViewById(R.id.slideButton_colors)).getLayoutParams().width = (int) (width * 0.075);
        ((ImageView) findViewById(R.id.slideButton_colors)).getLayoutParams().height = (int) (height * 0.222);

        RelativeLayout colors_layout = (RelativeLayout) findViewById(R.id.drawer_colors_layout);
        String[] colors = new String[]{"#000000", "#ffffff", "#180053", "#094db5", "#008ca1", "#595959", "#b81c46", "#009e00", "#97008f", "#d39927", "#fbd00c", "#5938b4", "#dddddd", "#1a9a6e", "#cb4848", "#0d4373"};
        int margin = (int) (width * 0.02);
        int margin1 = (int) (width * 0.02) - 1;
        int size = (int) (0.065 * height);
        int chk = 0;
        for (int i = 0; i < 16; i++) {

            View view = new View(this);
            view.setBackgroundColor(Color.WHITE);
            rParams = new LayoutParams(size + 2, size + 2);
            rParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            rParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            rParams.leftMargin = margin - 1;
            rParams.topMargin = margin1 - 1;
            colors_layout.addView(view, rParams);

            ImageView img = new ImageView(this);
            img.setBackgroundColor(Color.parseColor(colors[i]));
            img.setTag(colors[i]);
            rParams = new LayoutParams(size, size);
            rParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            rParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            rParams.leftMargin = margin;
            rParams.topMargin = margin1;
            colors_layout.addView(img, rParams);
            chk++;
            margin = margin + size + (int) (width * 0.02);
            if (chk == 2) {
                chk = 0;
                margin = (int) (width * 0.02);
                margin1 = margin1 + size + (int) (width * 0.02);
            }

            img.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    ImageView view = (ImageView) v;
                    Log.v("Color", "" + view.getTag());
                    panel.change_color("" + view.getTag());
                    ((SlidingDrawer) findViewById(R.id.SlidingDrawer_colors)).animateClose();
//					panel.setVisibility(View.VISIBLE);
                    delete.bringToFront();
                    next.bringToFront();
                }
            });


        }

        /**
         * Buttons Sliding Drawer
         */
//		((SlidingDrawer)findViewById(R.id.SlidingDrawer_buttons)).getLayoutParams().width = (int) (width*0.175) + (int) (width*0.075);
//		((SlidingDrawer)findViewById(R.id.SlidingDrawer_buttons)).getLayoutParams().height = (int) (height*0.18);
        rParams = new LayoutParams((int) (width * 0.175) + (int) (width * 0.075), (int) (height * 0.18));
        rParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        rParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        rParams.topMargin = (int) (height * 0.1) + ((int) (height * 0.63) / 2) + ((int) (height * 0.222) / 2) + 10;
        ((SlidingDrawer) findViewById(R.id.SlidingDrawer_buttons)).setLayoutParams(rParams);


        ((ImageView) findViewById(R.id.slideButton_buttons)).getLayoutParams().width = (int) (width * 0.075);
        ((ImageView) findViewById(R.id.slideButton_buttons)).getLayoutParams().height = (int) (height * 0.09);

        RelativeLayout buttons_layout = (RelativeLayout) findViewById(R.id.scroll_buttons);

        ImageButton flipx = new ImageButton(this);
        flipx.setBackgroundResource(R.drawable.flipx_selector);
        flipx.setId(1);
        rParams = new LayoutParams((int) (0.144 * width), (int) (0.1213 * width));
        rParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        rParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        rParams.topMargin = (int) (0.016 * height);
        buttons_layout.addView(flipx, rParams);
        flipx.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Matrix flipHorizontalMatrix = new Matrix();
                flipHorizontalMatrix.setScale(-1, 1);
                flipHorizontalMatrix.postTranslate(workingBitmap.getWidth(), 0);
                workingBitmap = Bitmap.createBitmap(workingBitmap, 0, 0, workingBitmap.getWidth(), workingBitmap.getHeight(), flipHorizontalMatrix, true);
                image.setImageBitmap(workingBitmap);
            }
        });

        ImageButton flipy = new ImageButton(this);
        flipy.setBackgroundResource(R.drawable.flipy_selector);
        rParams = new LayoutParams((int) (0.144 * width), (int) (0.1213 * width));
        rParams.addRule(RelativeLayout.BELOW, flipx.getId());
        rParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        rParams.bottomMargin = (int) (0.016 * height);
        buttons_layout.addView(flipy, rParams);
        flipy.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Matrix flipVerticalMatrix = new Matrix();
                flipVerticalMatrix.setScale(1, -1);
                flipVerticalMatrix.postTranslate(workingBitmap.getWidth(), 0);
                workingBitmap = Bitmap.createBitmap(workingBitmap, 0, 0, workingBitmap.getWidth(), workingBitmap.getHeight(), flipVerticalMatrix, true);
                image.setImageBitmap(workingBitmap);
            }
        });
        /**
         * Filters Sliding Drawer
         */

//		((SlidingDrawer)findViewById(R.id.SlidingDrawer_filters)).getLayoutParams().width = (int) (width*0.1875) + (int) (width*0.075);
//		((SlidingDrawer)findViewById(R.id.SlidingDrawer_filters)).getLayoutParams().height = (int) (height*0.534);
        rParams = new LayoutParams((int) (width * 0.1875) + (int) (width * 0.075), (int) (height * 0.534));
        rParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        rParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        rParams.topMargin = (int) (height * 0.1) + (int) (height * 0.0242);
        ((SlidingDrawer) findViewById(R.id.SlidingDrawer_filters)).setLayoutParams(rParams);


        ((ImageView) findViewById(R.id.slideButton_filters)).getLayoutParams().width = (int) (width * 0.075);
        ((ImageView) findViewById(R.id.slideButton_filters)).getLayoutParams().height = (int) (height * 0.15);

        int[] seletors = new int[]{R.drawable.normal_selector, R.drawable.block_selector, R.drawable.blur_selector, R.drawable.gray_selector, R.drawable.light_filter, R.drawable.lomo_filter, R.drawable.oil_selector, R.drawable.old_filter, R.drawable.sharpen_selector, R.drawable.sketch_selector, R.drawable.soft_selector};

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.filter_hscroll_layout);
        int top_margin = (int) (0.0125 * width);
        for (int i = 0; i < 11; i++) {

            ImageView btn = new ImageButton(this);
            btn.setBackgroundResource(seletors[i]);
            btn.setId(i);
            rParams = new LayoutParams((int) (width * 0.139), (int) (height * 0.138));
            rParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            rParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            rParams.topMargin = top_margin;
            rParams.leftMargin = (int) (0.0125 * width) + (int) (0.0125 * width);
            top_margin = top_margin + (int) (height * 0.138) + (int) (0.0125 * width);
            layout.addView(btn, rParams);

            btn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View view) {
                    // TODO Auto-generated method stub
                    ProgressDialog dialog = new ProgressDialog(PaintActivity.this);
                    dialog.setCancelable(false);
                    ImageView v = (ImageView) view;
                    Aysnc_Task task = new Aysnc_Task(dialog, v.getId(), PaintActivity.this, 1);
                    task.execute("");
                }
            });
        }

        /**
         * Edit Buttons
         */
        delete = (ImageButton) findViewById(R.id.paint_delete);
        rParams = new LayoutParams((int) (0.139 * width), (int) (0.139 * width));
        rParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        rParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        rParams.leftMargin = (int) (width * 0.025);
        rParams.bottomMargin = (int) (width * 0.025);
        delete.setLayoutParams(rParams);
        delete.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Log.e("PAint", "delete");
                panel.remove_color();
            }
        });

        next = (ImageButton) findViewById(R.id.paint_next);
        rParams = new LayoutParams((int) (0.139 * width), (int) (0.139 * width));
        rParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        rParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        rParams.rightMargin = (int) (width * 0.025);
        rParams.bottomMargin = (int) (width * 0.025);
        next.setLayoutParams(rParams);
        next.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                ((SlidingDrawer) findViewById(R.id.SlidingDrawer_buttons)).setVisibility(View.INVISIBLE);
                ((SlidingDrawer) findViewById(R.id.SlidingDrawer_colors)).setVisibility(View.INVISIBLE);
                ((SlidingDrawer) findViewById(R.id.SlidingDrawer_filters)).setVisibility(View.INVISIBLE);
                ((ImageButton) findViewById(R.id.paint_delete)).setVisibility(View.INVISIBLE);
                ((ImageButton) findViewById(R.id.paint_next)).setVisibility(View.INVISIBLE);
                ProgressDialog dialog = new ProgressDialog(PaintActivity.this);
                dialog.setCancelable(false);
                Aysnc_Task task = new Aysnc_Task(dialog, 0, PaintActivity.this, 2);
                task.execute("");
            }
        });

        ((SlidingDrawer) findViewById(R.id.SlidingDrawer_colors)).setOnDrawerOpenListener(new OnDrawerOpenListener() {

            @Override
            public void onDrawerOpened() {
                ((SlidingDrawer) findViewById(R.id.SlidingDrawer_buttons)).close();
                ((SlidingDrawer) findViewById(R.id.SlidingDrawer_filters)).close();
            }
        });
        ((SlidingDrawer) findViewById(R.id.SlidingDrawer_buttons)).setOnDrawerOpenListener(new OnDrawerOpenListener() {

            @Override
            public void onDrawerOpened() {
                ((SlidingDrawer) findViewById(R.id.SlidingDrawer_colors)).close();
                ((SlidingDrawer) findViewById(R.id.SlidingDrawer_filters)).close();
            }
        });
        ((SlidingDrawer) findViewById(R.id.SlidingDrawer_filters)).setOnDrawerOpenListener(new OnDrawerOpenListener() {

            @Override
            public void onDrawerOpened() {
                ((SlidingDrawer) findViewById(R.id.SlidingDrawer_buttons)).close();
                ((SlidingDrawer) findViewById(R.id.SlidingDrawer_colors)).close();
            }
        });
    }

    public void Apply() {
        ((ImageView) findViewById(R.id.paint_image)).setImageBitmap(workingBitmap);
    }

    public void Apply_Filter(int id) {
        if (null != workingBitmap && !workingBitmap.isRecycled()) {
            workingBitmap.recycle();
            workingBitmap = null;
//			System.gc();
        }
        workingBitmap = BitmapFactory.decodeFile(path);
        workingBitmap = BitmapFilter.changeStyle(workingBitmap, id);
    }

    public void closedrawers() {
        ((SlidingDrawer) findViewById(R.id.SlidingDrawer_buttons)).close();
        ((SlidingDrawer) findViewById(R.id.SlidingDrawer_colors)).close();
        ((SlidingDrawer) findViewById(R.id.SlidingDrawer_filters)).close();

    }

    public void saveimage() {

        int imageNum = 0;
        File imagesFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "SnapChat");
        if (!imagesFolder.exists()) {
            imagesFolder.mkdirs();
        }
        String fileName = "SnapChatimage" + String.valueOf(imageNum) + ".jpg";
        Bitmap bitmap1 = null;
        output = new File(imagesFolder, fileName);
        while (output.exists()) {
            imageNum++;
            fileName = "PicPlayPost image" + String.valueOf(imageNum) + ".jpg";
            output = new File(imagesFolder, fileName);
        }

//	        if(null != bitmap1 && !bitmap1.isRecycled()){
//				bitmap1.recycle();
//			}

        ((RelativeLayout) findViewById(R.id.paint_main)).setDrawingCacheEnabled(true);
        ((RelativeLayout) findViewById(R.id.paint_main)).buildDrawingCache();

        try {
            bitmap1 = Bitmap.createBitmap(((RelativeLayout) findViewById(R.id.paint_main)).getDrawingCache());
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Image too large", Toast.LENGTH_LONG).show();
        }
        ((RelativeLayout) findViewById(R.id.paint_main)).setDrawingCacheEnabled(false);

        bitmap1 = Bitmap.createBitmap(bitmap1, 0, 0, width, height);
        Log.e("pager", "" + bitmap1.getHeight() + bitmap1.getWidth());

        OutputStream fOut = null;
        try {
            fOut = new FileOutputStream(output);
            bitmap1.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void start() {
        Intent intent = new Intent(PaintActivity.this, ContactsActivity.class);
        intent.putExtra("path", output.toString());
        Log.e("outputfile", "11" + output.toString());
        startActivity(intent);
        finish();
    }

}

class Aysnc_Task extends AsyncTask<String, String, String> {

    private ProgressDialog pDialog;
    PaintActivity paint;
    int id, Func_id;

    public Aysnc_Task(ProgressDialog dialog, int id, PaintActivity paint, int func_id) {
        this.pDialog = dialog;
        this.paint = paint;
        this.id = id;
        this.Func_id = func_id;
    }

    @Override
    protected void onPreExecute() {
        // TODO Auto-generated method stub
        super.onPreExecute();
        pDialog.show();
    }

    @Override
    protected String doInBackground(String... params) {
        if (Func_id == 1) {
            paint.Apply_Filter(id);
        } else {
            paint.saveimage();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        // TODO Auto-generated method stub
        super.onPostExecute(result);
        if (Func_id == 1) {
            paint.Apply();
        } else {
            paint.start();
        }
        pDialog.dismiss();

    }

}