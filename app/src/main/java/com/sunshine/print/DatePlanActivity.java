package com.sunshine.print;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Toast;

import com.sunshine.print.adapter.BillAdapter;
import com.sunshine.print.adapter.BillAdapter2;
import com.sunshine.print.adapter.DailyPlanAdapter;
import com.sunshine.print.db.DBHelper;
import com.sunshine.print.eventbus.Event;
import com.sunshine.print.excel.ExcelUtils;
import com.sunshine.print.object.BillObject;
import com.sunshine.print.object.PlanObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class DatePlanActivity extends AppCompatActivity {

    @InjectView(R.id.back)
    Button back;
    @InjectView(R.id.dateplan_listview)
    ListView dateplanListview;
    @InjectView(R.id.date_plan2)
    Button datePlan2;

    private DBHelper mDbHelper;

    private Dialog loadingExcel;
    protected List<PlanObject> plans = new ArrayList<>();
    protected DailyPlanAdapter dailyPlanAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_plan);
        ButterKnife.inject(this);
        EventBus.getDefault().register(this);
       // mDbHelper = new DBHelper(this);
        mDbHelper = new DBHelper(this,"SunShine.db",null,3);
        mDbHelper.open();


    }

    public void onEventMainThread(Event event) {
        if (event.getActionType() == Event.RFID2) {
//            rfid4 = event.getMessage();
//            rfidOn.setText(rfid4);
        }
    }


    @OnClick({R.id.back, R.id.date_plan2})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.date_plan2:
                showDialog(0);

                break;
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == 0) {
            Map<String, Integer> images = new HashMap<String, Integer>();
            // 下面几句设置各文件类型的图标， 需要你先把图标添加到资源文件夹
            images.put(OpenFileDialog.sRoot, R.drawable.skin_big_green); // 根目录图标
            images.put(OpenFileDialog.sParent, R.drawable.skin_big_red); // 返回上一层的图标
            images.put(OpenFileDialog.sFolder, R.drawable.skin_big_yellow); // 返回上一层的图标
            images.put("xls", R.drawable.skin_blue); // mp4文件图标
            images.put(OpenFileDialog.sEmpty, R.drawable.skin_unusual);
            loadingExcel = OpenFileDialog.createDialog(id, this, "打开日计划文件",
                    new CallbackBundle() {
                        @Override
                        public void callback(Bundle bundle) {
                            String path = bundle.getString("path");
                            // Log.e(TAG, path);
                            plans = ExcelUtils.read2DB(new File(path), DatePlanActivity.this);
                            dailyPlanAdapter = new DailyPlanAdapter(DatePlanActivity.this, plans, onClickListener);//适配数据
                            dateplanListview.setAdapter(dailyPlanAdapter);//显示数据
                            try {
                                Workbook course = null;
                                course = Workbook.getWorkbook(new File(path));
                                Sheet sheet = course.getSheet(0);
                                Cell cell = null;

                                for (int i = 1; i < sheet.getRows(); i++) {
                                    ContentValues values = new ContentValues();
                                    cell = sheet.getCell(0, i);
                                    values.put("sn", cell.getContents());
                                    cell = sheet.getCell(1, i);
                                    values.put("pass", cell.getContents());
                                    cell = sheet.getCell(2, i);
                                    values.put("mac", cell.getContents());
                                    cell = sheet.getCell(3, i);
                                    values.put("pno", cell.getContents());
                                    cell = sheet.getCell(4, i);
                                    values.put("enc", cell.getContents());
                                    cell = sheet.getCell(5, i);
                                    values.put("date", cell.getContents());
                                    cell = sheet.getCell(6, i);
                                    values.put("des", cell.getContents());
                                    cell = sheet.getCell(7, i);
                                    values.put("key", cell.getContents());
                                    values.put("print","未打印");
                                    values.put("type","0");
                                    values.put("version","0");

//                                    String sn1 = sheet.getCell(0,1).getContents();
//                                    Log.d("FF",sn1);
                                   // Cursor cur = mDbHelper.exeSql("SELECT * FROM dateplan2 ");
                                   // "select id from table where id= '"+id+"'";
                                //    Cursor cur = mDbHelper.exeSql("SELECT * FROM dateplan2 WHERE sn = " + sheet.getCell(0,1).getContents());
                                      Cursor cur = mDbHelper.exeSql("SELECT sn FROM dateplan2 WHERE sn = '" +sheet.getCell(0,i).getContents()+"'");
                                   // Log.d("HH", cur.getString(cur.getColumnIndexOrThrow("sn")));
                                    String sn2 = null;
                                    if(cur.moveToFirst()){
                                            String sn1  = cur.getString(cur.getColumnIndexOrThrow("sn"));
                                            Log.d("DD", sn1);
                                            sn2 = sn1;

                                    }

                                    if ( sn2 != null)
                                    {

                                    }else {
                                        mDbHelper.insert("dateplan2", values);
                                    }
                                    String sn3 = null;
                                    sn2 = sn3;
                                    values.clear();

                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (BiffException e) {
                                e.printStackTrace();
                            }



                        }


                    }, ".xls;", images);
            return loadingExcel;
        }
        return null;
    }

    /**
     * item中button点击事件
     */
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };

    @Override
    protected void onDestroy() {
        //注销EventBus
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
