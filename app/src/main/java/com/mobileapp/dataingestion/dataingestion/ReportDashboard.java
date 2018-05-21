package com.mobileapp.dataingestion.dataingestion;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ReportDashboard extends Activity {
    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;
    List<String> expandableListTitle;
    HashMap<String, ArrayList<ReportDetails>> expandableListDetail=new HashMap<String, ArrayList<ReportDetails>>();
    String response = null;
    private final String DashboardAPI="GetDashboardList";
    private final String CATEGORY="Category";
    private final String DASHBOARD="Dashboard";
    private final String DID="Id";
    private final String REPORTNAME="RName";
    private final String REPORTLINK="RLink";
    private final String REPORTCATEGORY="RCategory";
    private int lastExpandedPosition = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_dashboard);
        this.expandableListView =(ExpandableListView)findViewById(R.id.expandableListView);
        try
        {

            if(!AppData.isOnline(ReportDashboard.this))
            {
                Toast.makeText(ReportDashboard.this,AppData.NOINTERNETMESSAGE,Toast.LENGTH_LONG).show();
                return;
            }
            getReportData();
        }catch (Exception ex)
        {
            Toast.makeText(ReportDashboard.this,AppData.ASYNCEXCEPTIONMESSAGE,Toast.LENGTH_LONG).show();
        }
    }

    private void getReportData()
    {
        SharedPreferences sharedPref = getSharedPreferences(AppData.SHAREDPREF, MODE_PRIVATE);
        if (sharedPref.contains(AppData.SHAREDPREFCLIENTID)) {
            String clientId =(sharedPref.getString(AppData.SHAREDPREFCLIENTID, ""));
            if(clientId.equals(""))
            {
                Toast.makeText(ReportDashboard.this,AppData.SHAREDPREFMESSAGE,Toast.LENGTH_LONG).show();
                return;
            }
            ReportDashboard.ReportsTask task = new ReportDashboard.ReportsTask();
            task.execute(new String[]{clientId});

        }
        else
        {
            Toast.makeText(ReportDashboard.this,AppData.SHAREDPREFMESSAGE,Toast.LENGTH_LONG).show();
            return;
        }
       // String clientId = (shared.getString("ClientId", ""));
       // String clientId = "1";
        //ReportDashboard.ReportsTask task = new ReportDashboard.ReportsTask();
       // task.execute(new String[]{clientId});


    }


    private class ReportsTask extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            String weather = "UNDEFINED";
            String res = null;
            try {
                ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
                postParameters.add(new BasicNameValuePair("clientId", params[0] ));
                response = CustomHttpClient.executeHttpPost( AppData.URLPath+DashboardAPI, postParameters);
                res=response.toString();
               // res= res.replaceAll("\\s+","");
            }
            catch (Exception e) {}
            return res;
        }//close doInBackground
        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(ReportDashboard.this,"","Please wait...");
        }

        @Override
        protected void onPostExecute(String result) {
            if (result!=null && result!=""){
                try
                {
                    JSONArray mainArray = new JSONArray(result);
                    for(int i=0;i<mainArray.length();i++)
                    {
                        JSONObject jsonObj= mainArray.getJSONObject(i);
                        String  category = jsonObj.getString(CATEGORY).trim();
                        JSONArray catArray = jsonObj.getJSONArray(DASHBOARD);
                       ArrayList<ReportDetails> lstReport=new ArrayList<ReportDetails>();
                        for(int x=0;x<catArray.length();x++)
                        {
                            ReportDetails RDetails=new ReportDetails();
                            JSONObject jObj= catArray.getJSONObject(x);
                            RDetails.ReportId=jObj.getString(DID).trim();
                            RDetails.ReportName=jObj.getString(REPORTNAME).trim();
                            RDetails.ReportLink=jObj.getString(REPORTLINK).trim();
                            RDetails.ReportCategory=jObj.getString(REPORTCATEGORY).trim();
                            lstReport.add(RDetails);
                        }
                        expandableListDetail.put(category,lstReport);
                        }
                    } catch (JSONException e) {
                    e.printStackTrace();
                }
                expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
                expandableListAdapter = new CustomExpandableListAdapter(ReportDashboard.this, expandableListTitle, expandableListDetail);
                expandableListView.setAdapter(expandableListAdapter);
                expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
                    @Override
                    public void onGroupExpand(int groupPosition) {
//                        Toast.makeText(getApplicationContext(),
//                                expandableListTitle.get(groupPosition) + " List Expanded.",
//                                Toast.LENGTH_SHORT).show();
                        //collapse all other expanded group
                        if (lastExpandedPosition != -1
                                && groupPosition != lastExpandedPosition) {
                            expandableListView.collapseGroup(lastExpandedPosition);
                        }
                        lastExpandedPosition = groupPosition;
                    }
                });

                expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
                    @Override
                    public void onGroupCollapse(int groupPosition) {
//                        Toast.makeText(getApplicationContext(),
//                                expandableListTitle.get(groupPosition) + " List Collapsed.",
//                                Toast.LENGTH_SHORT).show();

                    }
                });

                expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                    @Override
                    public boolean onChildClick(ExpandableListView parent, View v,
                                                int groupPosition, int childPosition, long id) {
                        try{
                            ArrayList<ReportDashboard.ReportDetails> reportDetails=expandableListDetail.get(expandableListTitle.get(groupPosition));
                            String rprtLnk=reportDetails.get(childPosition).ReportLink;
//                            Toast.makeText(
//                                    getApplicationContext(),
//                                    rprtLnk, Toast.LENGTH_SHORT
//                            ).show();
                            Intent i = new Intent(ReportDashboard.this,ReportDisplay.class);
                            i.putExtra("ReportLink", rprtLnk);
                            startActivity(i);
                        }
                        catch (Exception ex)
                        {
                            Toast.makeText(ReportDashboard.this,AppData.GENERICMESSAGE,Toast.LENGTH_LONG).show();
                        }

                        return false;
                    }
                });
                }
            else
            {
                Toast.makeText(ReportDashboard.this,AppData.ASYNCEXCEPTIONMESSAGE,Toast.LENGTH_LONG).show();
            }
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }//close onPostExecute
    }// close validateUserTask

    public class ReportDetails
    {
        public String ReportId;
        public String ReportName;
        public String ReportLink;
        public String ReportCategory;

    }
}