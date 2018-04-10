package com.example.tal.minesweeper;


import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.example.tal.minesweeper.Logic.Record;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentTable extends DialogFragment {

    private Button button;
    private boolean wait = true;

    // views values
    private final int TEXT_SIZE = 11;
    private final int PADDING_WIDTH = ((Resources.getSystem().getDisplayMetrics().widthPixels/4) - 80);
    private final int PADDING_HIGHT = 8;
    private final int LEFT = 1;
    private final int ADDRESS = 2;
    private final int RIGHT = 4;

    // data text values
    private static int idCounter = 0;

    private final int EASY = 0;
    private final int MEDIUM = 1;
    private final int HARD = 2;

    // table fragment members
    private int currentLevel;
    private RadioButton defaultButton;
    private View rootView;
    private TableLayout tableLayout;

    private TextView tvAddress;
    private int i;

    public FragmentTable() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_table, container, false);
        tableLayout = (TableLayout) rootView.findViewById(R.id.scoreTable);

        button = (Button)rootView.findViewById(R.id.exitButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(wait) {
                    dismiss();
                }
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    wait = false;
                    Thread.sleep(2000);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            defaultButton.setChecked(true);
                            rootView.findViewById(R.id.progressBarTable).setVisibility(View.GONE);
                            tableLayout.setVisibility(View.VISIBLE);
                            wait = true;
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();


        defaultButton = (RadioButton)rootView.findViewById(R.id.tableRadioButton1);

        RadioGroup tableGroup = (RadioGroup) rootView.findViewById(R.id.rg_level_Table); //we are making a new radio group
        tableGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group , int checkedId) { // we are listening to the radio group on changing radio bottons
                defaultButton = (RadioButton) group.findViewById(checkedId);

                switch (defaultButton.getId()) {
                    case R.id.tableRadioButton1:
                        currentLevel = EASY;
                        break;

                    case R.id.tableRadioButton2:
                        currentLevel = MEDIUM;
                        break;

                    case R.id.tableRadioButton3:
                        currentLevel = HARD;
                        break;
                }
                tableLayout.removeAllViewsInLayout();
                initTable();
            }
        });
        // init table view
        return rootView;
    }

    // sets the TextView values
    private void setTextView(TextView tv, int id , int position)
    {
        tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        tv.setId(id);
        if(position == LEFT){
            tv.setPadding(40, PADDING_HIGHT, PADDING_WIDTH - tv.getText().length(), PADDING_HIGHT);
        }else if(position == RIGHT){
            tv.setPadding(0, PADDING_HIGHT, 40, PADDING_HIGHT);
        }else if(position == ADDRESS){
            tv.setPadding(40, PADDING_HIGHT, PADDING_WIDTH - tv.getText().length()
                    , PADDING_HIGHT);
        }else{
            tv.setPadding(0 , PADDING_HIGHT, PADDING_WIDTH - tv.getText().length(), PADDING_HIGHT);
        }

        tv.setTextSize(TEXT_SIZE);
        tv.setTextColor(Color.BLACK);
    }

    private void initTable(){
        int placementCounter = 0;
        ArrayList<Record> records = getRecordsByDifficulty();
        // add rows in table - if exist in data base
        for(i = 0 ; i < records.size() ; i++) {
            Record record  = records.get(i);

            TableRow tbrow = new TableRow(getActivity().getApplicationContext());
            tbrow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

            TextView tvPlacement = new TextView(getActivity().getApplicationContext());
            TextView tvName = new TextView(getActivity().getApplicationContext());
            tvAddress = new TextView(getActivity().getApplicationContext());
            TextView tvScore = new TextView(getActivity().getApplicationContext());

            tvPlacement.setText(++placementCounter+"");
            tvName.setText(record.getmName());
            tvScore.setText(record.getmGameTime()+"");
            tvAddress.setText(record.getAddress().substring(record.getAddress().lastIndexOf(',')+1,record.getAddress().length()));

            setTextView(tvPlacement, idCounter++, LEFT);
            setTextView(tvScore, idCounter++, 0);
            setTextView(tvAddress, idCounter++, ADDRESS);
            setTextView(tvName, idCounter++, RIGHT);

            tbrow.addView(tvPlacement);
            tbrow.addView(tvName);
            tbrow.addView(tvAddress);
            tbrow.addView(tvScore);
            // add row to table
            tableLayout.addView(tbrow, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
        }
    }
    private ArrayList<Record> getRecordsByDifficulty(){
        if(currentLevel == EASY){
            return MainActivity.getEasyRecords();
        }else if(currentLevel == MEDIUM){
            return MainActivity.getMediumRecords();
        }else{
            return MainActivity.getHardRecords();
        }
    }
}





