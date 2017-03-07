package com.sunshine.print;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sunshine.print.adapter.CodeAdapter;
import com.sunshine.print.adapter.PrintAdapter;
import com.sunshine.print.db.DBHelper;
import com.sunshine.print.event.ComEvent;
import com.sunshine.print.excel.ExcelUtils;
import com.sunshine.print.object.QrCode;
import com.sunshine.print.service.ComService;
import com.sunshine.print.util.L;
import com.sunshine.print.widget.AbstractSpinerAdapter;
import com.sunshine.print.widget.AbstractSpinerAdapter2;
import com.sunshine.print.widget.SpinerPopWindow;
import com.sunshine.print.widget.SpinerPopWindow2;

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

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AbstractSpinerAdapter.IOnItemSelectListener, AbstractSpinerAdapter2.IOnItemSelectListener2, AbstractSpinerAdapter2.IOnItemLongClickListener, AbstractSpinerAdapter.IOnItemLongClickListener {

    @InjectView(R.id.date_plan)
    Button datePlan;
    @InjectView(R.id.plan_put)
    Button planPut;
    @InjectView(R.id.print_num)
    EditText printNum;
    @InjectView(R.id.print_listview)
    ListView printListview;
    @InjectView(R.id.tv_pre)
    TextView tvPre;
    @InjectView(R.id.tv_value)
    TextView tvValue;
    @InjectView(R.id.bt_dropdown)
    ImageButton btDropdown;
    @InjectView(R.id.ll_lowwarn)
    RelativeLayout llLowwarn;
    @InjectView(R.id.fl_layout)
    FrameLayout flLayout;
    @InjectView(R.id.type_edit)
    EditText typeEdit;
    @InjectView(R.id.type_button)
    Button typeButton;
    @InjectView(R.id.version_edit)
    EditText versionEdit;
    @InjectView(R.id.version_button)
    Button versionButton;
    @InjectView(R.id.tv_pre2)
    TextView tvPre2;
    @InjectView(R.id.tv_value2)
    TextView tvValue2;
    @InjectView(R.id.bt_dropdown2)
    ImageButton btDropdown2;
    @InjectView(R.id.ll_lowwarn2)
    RelativeLayout llLowwarn2;
    @InjectView(R.id.fl_layout2)
    FrameLayout flLayout2;
    @InjectView(R.id.plan_delete)
    Button planDelete;
    @InjectView(R.id.num_edit)
    EditText numEdit;
    @InjectView(R.id.num_button)
    Button numButton;

    private PrintAdapter printAdapter;//库存信息的Adapter
    private List<String> timeList = new ArrayList<String>();
    private List<String> one_codeList = new ArrayList<String>();
    private List<String> two_codeList = new ArrayList<String>();
    private List<String> statusList = new ArrayList<String>();
    private DBHelper mDbHelper;

    private CodeAdapter cadapter;
    private List<QrCode> qrcodeList = new ArrayList<>();
    private boolean sun = false;
    private String twoCode, twoCode2;

    private File file;
    private String[] title = {"SN", "PASS", "MAC", "PNO", "ENCRYPTION", "DATE", "DESCRIPTION", "KEY"};
    private ArrayList<ArrayList<String>> bill2List;

    private List<String> typeList = new ArrayList<String>();
    private List<String> versionList = new ArrayList<String>();
    private String type = null;
    private String type2 = null;
    private String type3 = null;
    private String version = null;
    private String version2 = null;
    private String version3 = null;

    private String num_print = null;
    private String num = null;
    private String num2 = null;

    private Dialog loadingExcel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        bill2List = new ArrayList<ArrayList<String>>();

        cadapter = new CodeAdapter(qrcodeList, this);
        printListview.setAdapter(cadapter);//显示数据
        EventBus.getDefault().register(this);
        startService(new Intent(this, ComService.class));

        mDbHelper = new DBHelper(this, "SunShine.db", null, 3);
        mDbHelper.open();

        get1();

        Cursor cur2 = mDbHelper.exeSql("SELECT * FROM dateplan2");
        if (cur2.moveToFirst()) {
            do {
                version2 = cur2.getString(cur2.getColumnIndex("version"));
                type2 = cur2.getString(cur2.getColumnIndex("type"));
                String sn = cur2.getString(cur2.getColumnIndex("sn"));

                Log.d("VV", sn);
            } while (cur2.moveToNext());
        }
        cur2.close();
        typeEdit.setText(type2);
        versionEdit.setText(version2);

        Cursor cur1 = mDbHelper.exeSql("SELECT * FROM num");
        if (cur1.moveToFirst()) {
            do {
                 num = cur1.getString(cur1.getColumnIndex("num"));
                 num2 = cur1.getString(cur1.getColumnIndex("printnum"));

                //  Log.d("CC", version);
            } while (cur1.moveToNext());
        }
        cur1.close();
        numEdit.setText(num);
       // Log.d("LL", num2);
        setupViews();

    }


    public void onEventMainThread(ComEvent event) {
        if (event.getActionType() == ComEvent.ACTION_GET_CODE) {
            String code = event.getMessage();
            EventBus.getDefault().post(new ComEvent(code, ComEvent.ACTION_PRINT));
        }else if(event.getActionType() == ComEvent.ACTION_PRINT_NUM){
            String code = event.getMessage();
            printNum.setText(code);
            if(Integer.parseInt(printNum.getText().toString())>= Integer.parseInt(numEdit.getText().toString())){
                LayoutInflater factory = LayoutInflater.from(this);
                View myView = factory.inflate(R.layout.dialog1, null);
                ContextThemeWrapper cw = new ContextThemeWrapper(this, R.style.AlertDialogTheme);
                AlertDialog dialog = new AlertDialog.Builder(cw)
                        .setIcon(R.drawable.dialog)
                        .setTitle("报警信息")
                        .setView(myView)//使用自定义布局
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                dialog.getWindow().setLayout(650, 350);
            }
        }
    }

    // 打码假数据
    public void get1() {
        for (int j = 3; j < 10; j++) {
            timeList.add("2017.2.18");
            one_codeList.add("123456789");
            two_codeList.add("123456789987654321");//库存
            statusList.add("已打印");//报警
        }
        printAdapter = new PrintAdapter(timeList, one_codeList, two_codeList, statusList, MainActivity.this);
        printListview.setAdapter(printAdapter);
    }

    private void setupViews() {
        //  btDropdown.setOnClickListener(this);
//        String[] names = getResources().getStringArray(R.array.hero_name);
//        for (int i = 0; i < names.length; i++) {
//            typeList.add(names[i]);
//        }

        Cursor cur2 = mDbHelper.exeSql("SELECT * FROM type");
        if (cur2.moveToFirst()) {
            do {
                String type2 = cur2.getString(cur2.getColumnIndex("type"));
                typeList.add(type2);

                Log.d("CC", type2);
            } while (cur2.moveToNext());
        }
        cur2.close();

        Cursor cur = mDbHelper.exeSql("SELECT * FROM version");
        if (cur.moveToFirst()) {
            do {
                String version = cur.getString(cur.getColumnIndex("version"));
                versionList.add(version);

                //  Log.d("CC", version);
            } while (cur.moveToNext());
        }
        cur.close();


        mSpinerPopWindow = new SpinerPopWindow(this);
        mSpinerPopWindow.refreshData(typeList, 0);
        mSpinerPopWindow.setItemListener(this);
        mSpinerPopWindow.setItemLongListener(this);

        mSpinerPopWindow2 = new SpinerPopWindow2(this);
        mSpinerPopWindow2.refreshData(versionList, 0);
        mSpinerPopWindow2.setItemListener(this);
        mSpinerPopWindow2.setItemLongListener(this);
    }


    @Override
    protected void onDestroy() {
        //注销EventBus
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @SuppressLint("SimpleDateFormat")
    public void initExcelData() {
        file = new File(getSDPath() + "/SunShine");
        makeDir(file);
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        ExcelUtils.initExcel(file.toString() + "/sunshine.xls", title);
        ExcelUtils.writeObjListToExcel(getQrcodeData(), getSDPath()
                + "/SunShine/sunshine.xls", this);
    }

    public static void makeDir(File dir) {
        if (!dir.getParentFile().exists()) {
            makeDir(dir.getParentFile());
        }
        dir.mkdir();
    }

    public String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();
        }
        String dir = sdDir.toString();
        return dir;
    }

    private ArrayList<ArrayList<String>> getQrcodeData() {
        Cursor mCrusor = mDbHelper.exeSql("select * from dateplan2");
        while (mCrusor.moveToNext()) {
            ArrayList<String> beanList = new ArrayList<String>();
            beanList.add(mCrusor.getString(1));
            beanList.add(mCrusor.getString(2));
            beanList.add(mCrusor.getString(3));
            beanList.add(mCrusor.getString(4));
            beanList.add(mCrusor.getString(5));
            beanList.add(mCrusor.getString(6));
            beanList.add(mCrusor.getString(7));
            beanList.add(mCrusor.getString(8));
            bill2List.add(beanList);
            // Log.e(TAG,"bill2List："+new Gson().toJson(bill2List));
        }
        mCrusor.close();
        return bill2List;
    }


    @OnClick({R.id.date_plan, R.id.plan_put, R.id.bt_dropdown, R.id.type_button, R.id.version_button, R.id.ll_lowwarn, R.id.ll_lowwarn2, R.id.plan_delete, R.id.num_button})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.date_plan:
//                Intent intent = new Intent(MainActivity.this, DatePlanActivity.class);
//                startActivity(intent);
                showDialog(0);
                break;
            case R.id.plan_put:
                initExcelData();
                break;
            case R.id.plan_delete:
                SQLiteDatabase database = mDbHelper.getWritableDatabase();
                database.delete("dateplan2", "version = ?", new String[]{typeEdit.getText().toString()});
                break;
//            case R.id.bt_dropdown:
//                showSpinWindow();
//                break;
            case R.id.ll_lowwarn:
                showSpinWindow();
                break;
            case R.id.ll_lowwarn2:
                showSpinWindow2();
                break;
            case R.id.num_button:
                num_print = numEdit.getText().toString();
                if (num_print == null || ("".equals(num_print))) {
//                    ContentValues values2 = new ContentValues();
//                    values2.put("num", "0");
//                    values2.put("printnum","0");
//                    mDbHelper.insert("num", values2);
                } else {
                    SQLiteDatabase database2 = mDbHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put("num", num_print);
                    database2.update("num", values, null, null);
                }

                break;
            case R.id.type_button:
                type = typeEdit.getText().toString();
                if (type == null || ("".equals(type))) {

                } else {
                    ContentValues values = new ContentValues();
                    values.put("type", type);
                    Cursor cur = mDbHelper.exeSql("SELECT * FROM type WHERE type = '" + type + "'");
                    String sn2 = null;
                    if (cur.moveToFirst()) {
                        String sn1 = cur.getString(cur.getColumnIndexOrThrow("type"));
                        Log.d("DD", sn1);
                        sn2 = sn1;
                    }
                    if (sn2 != null) {
                    } else {
                        mDbHelper.insert("type", values);
                        typeList.add(type);
                        mSpinerPopWindow = new SpinerPopWindow(this);
                        mSpinerPopWindow.refreshData(typeList, 0);
                        mSpinerPopWindow.setItemListener(this);
                        mSpinerPopWindow.setItemLongListener(this);
                    }
                    String sn3 = null;
                    sn2 = sn3;
                    values.clear();
                }

                break;
            case R.id.version_button:
                version = versionEdit.getText().toString();
                if (version == null || ("".equals(version))) {

                } else {
                    ContentValues values = new ContentValues();
                    values.put("version", version);
                    Cursor cur = mDbHelper.exeSql("SELECT * FROM version WHERE version = '" + version + "'");
                    String sn2 = null;
                    if (cur.moveToFirst()) {
                        String sn1 = cur.getString(cur.getColumnIndexOrThrow("version"));
                        Log.d("DD", sn1);
                        sn2 = sn1;
                    }
                    if (sn2 != null) {
                    } else {
                        mDbHelper.insert("version", values);
                        versionList.add(version);
                        mSpinerPopWindow2 = new SpinerPopWindow2(this);
                        mSpinerPopWindow2.refreshData(versionList, 0);
                        mSpinerPopWindow2.setItemListener(this);
                        mSpinerPopWindow2.setItemLongListener(this);
                    }
                    String sn3 = null;
                    sn2 = sn3;
                    values.clear();
                }
                break;
        }
    }

    private void setHero(int pos) {
        if (pos >= 0 && pos <= typeList.size()) {
            String value = typeList.get(pos);
            tvValue.setText(value);
            SQLiteDatabase database = mDbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("type", value);
            database.update("dateplan2", values, null, null);

            Cursor cur2 = mDbHelper.exeSql("SELECT * FROM dateplan2");
            if (cur2.moveToFirst()) {
                do {
                    type3 = cur2.getString(cur2.getColumnIndex("type"));
                } while (cur2.moveToNext());
            }
            if (type3 == null || type3.equals("")) {
                Toast.makeText(this, "type值未设置成功，或者未导入日计划", Toast.LENGTH_LONG).show();
            } else {
                typeEdit.setText(type3);
                Toast.makeText(this, "type值设置成功", Toast.LENGTH_LONG).show();
            }
            cur2.close();
        }
    }

    private void setHero2(int pos) {
        if (pos >= 0 && pos <= versionList.size()) {
            String value = versionList.get(pos);
            tvValue2.setText(value);
            SQLiteDatabase database = mDbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("version", value);
            database.update("dateplan2", values, null, null);
            Cursor cur2 = mDbHelper.exeSql("SELECT * FROM dateplan2");
            if (cur2.moveToFirst()) {
                do {
                    version3 = cur2.getString(cur2.getColumnIndex("version"));
                } while (cur2.moveToNext());
            }
            L.e("wyg:" + value);
            L.e("wyg:" + version3);
            if (version3 == null || version3.equals("")) {
                Toast.makeText(this, "version值未设置成功，或者未导入日计划", Toast.LENGTH_LONG).show();
            } else {
                versionEdit.setText(version3);
                Toast.makeText(this, "version值设置成功", Toast.LENGTH_LONG).show();

            }
            cur2.close();

        }
    }

    private SpinerPopWindow mSpinerPopWindow;
    private SpinerPopWindow2 mSpinerPopWindow2;

    private void showSpinWindow() {
        Log.e("", "showSpinWindow");
        mSpinerPopWindow.setWidth(tvValue.getWidth());
        mSpinerPopWindow.showAsDropDown(tvValue);
    }

    private void showSpinWindow2() {
        mSpinerPopWindow2.setWidth(tvValue2.getWidth());
        mSpinerPopWindow2.showAsDropDown(tvValue2);
    }

    @Override
    public void onItemClick(int pos) {
        setHero(pos);
    }

    @Override
    public void onItemClick2(int pos) {
        setHero2(pos);
    }

    //长按version列表弹出对话框删除
    @Override
    public void onItemLongClick2(final int pos) {
        String value = null;
        if (pos >= 0 && pos <= versionList.size()) {
            value = versionList.get(pos);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setTitle("警告");
        builder.setMessage("是否删除version值");
        builder.setCancelable(false);
        final String finalValue = value;
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                SQLiteDatabase database = mDbHelper.getWritableDatabase();
                database.delete("version", "version = ?", new String[]{finalValue});
                versionList.remove(pos);
                mSpinerPopWindow2.refreshData(versionList, 0);

            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = builder.create();
        Window mWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = mWindow.getAttributes();
        lp.x = 800;   //新位置X坐标
        lp.y = 100; //新位置Y坐标
        dialog.onWindowAttributesChanged(lp);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        dialog.getWindow().setLayout(350, 180);

    }

    @Override
    public void onItemLongClick(final int pos) {

        String value = null;
        if (pos >= 0 && pos <= typeList.size()) {
            value = typeList.get(pos);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setTitle("警告");
        builder.setMessage("是否删除type值");
        builder.setCancelable(false);
        final String finalValue = value;
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                SQLiteDatabase database = mDbHelper.getWritableDatabase();
                database.delete("type", "type = ?", new String[]{finalValue});
                typeList.remove(pos);
                mSpinerPopWindow.refreshData(typeList, 0);

            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = builder.create();
        Window mWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = mWindow.getAttributes();
        lp.x = 800;   //新位置X坐标
        lp.y = 100; //新位置Y坐标
        dialog.onWindowAttributesChanged(lp);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        dialog.getWindow().setLayout(350, 180);

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
                            try {
                                Workbook course = null;
                                course = Workbook.getWorkbook(new File(path));
                                Sheet sheet = course.getSheet(0);
                                Cell cell = null;
                                L.e("wyg:" + "11");
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
                                    values.put("print", "未打印");
                                    values.put("type", "0");
                                    values.put("version", "0");

                                    Cursor cur = mDbHelper.exeSql("SELECT sn FROM dateplan2 WHERE sn = '" + sheet.getCell(0, i).getContents() + "'");
                                    String sn2 = null;
                                    if (cur.moveToFirst()) {
                                        String sn1 = cur.getString(cur.getColumnIndexOrThrow("sn"));
                                        Log.d("DD", sn1);
                                        sn2 = sn1;
                                    }
                                    cur.close();
                                    if (sn2 != null) {
                                    } else {
                                        mDbHelper.insert("dateplan2", values);
                                    }
                                    String sn3 = null;
                                    sn2 = sn3;
                                    values.clear();
                                }
                                L.e("wyg:" + "22");
                                Toast.makeText(getApplication(), "导入日计划完成", Toast.LENGTH_LONG).show();
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


}
