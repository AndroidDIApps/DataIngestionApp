package com.mobileapp.dataingestion.dataingestion;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;


public class Login extends Activity {

    EditText txt_uname, txt_pwd;
    TextView txt_Error;
    Button loginBtn;
    String response = null;
    private final String LoginAPI="AuthenticateUser";
    SharedPreferences pref ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.txt_uname=(EditText)findViewById(R.id.editUsrName);
        this.txt_pwd=(EditText)findViewById(R.id.editpwd);
        this.loginBtn =(Button)findViewById(R.id.btnLogin);
        //Login.this.getSystemService(Login.this.CONNECTIVITY_SERVICE);

        loginBtn.setOnClickListener(new Button.OnClickListener() {

            public void onClick(View view) {
                Log.i("Tag2", "Inside Onclick");
                System.out.println("It comes here");
                try{
                    if (!(txt_uname.getText().toString() != null && !txt_uname.getText().toString() .isEmpty() && txt_pwd.getText().toString() != null && !txt_pwd.getText().toString() .isEmpty()))
                    {
                        Toast.makeText(Login.this,AppData.LOGINFORMVALIDATIONMESSAGE,Toast.LENGTH_LONG).show();
                        return;
                    }
                    //checkInternetConnectivity();

                    if(!AppData.isOnline(Login.this))
                    {
                        Toast.makeText(Login.this,AppData.NOINTERNETMESSAGE,Toast.LENGTH_LONG).show();
                        return;
                    }
                    String uname = txt_uname.getText().toString();
                    String pwd = txt_pwd.getText().toString();
                    isUserAuthenticated(uname,pwd);//
                    }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void isUserAuthenticated(String userName,String pwd)
    {
        validateUserTask task = new validateUserTask();
        task.execute(new String[]{userName, pwd});
    }

    private class validateUserTask extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            String weather = "UNDEFINED";
            String res = null;
            try {
                ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
                postParameters.add(new BasicNameValuePair("userName", params[0] ));
                postParameters.add(new BasicNameValuePair("password", params[1] ));
                    response = CustomHttpClient.executeHttpPost( AppData.URLPath+LoginAPI, postParameters);
                    res=response.toString();
                    res= res.replaceAll("\\s+","");
                }
                catch (Exception e) {}
                return res;
        }//close doInBackground
        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(Login.this,"","Validating Please wait...");
        }

        @Override
        protected void onPostExecute(String result) {
            if (result!=null && result!=""){
            try
            {
                JSONObject mainObject = new JSONObject(result);
                String  userId = mainObject.getString("UserId");
                if(userId=="null" || userId ==null)
                {
                    Toast.makeText(Login.this,AppData.INVALIDCREDENTIALS,Toast.LENGTH_LONG).show();
                }
                else
                {
                    pref=  Login.this.getSharedPreferences(AppData.SHAREDPREF, 0); //
                    SharedPreferences.Editor editor = pref.edit();
                  editor.putString(AppData.SHAREDPREFCLIENTID,String.valueOf(mainObject.getInt("ClientId")));
                   // editor.putString("ClientId","1");
                    editor.commit();
                    Intent i = new Intent(Login.this, ReportDashboard.class);
                    startActivity(i);
                    finish();
                }
            }
            catch (Exception ex)
            {
                Toast.makeText(Login.this,AppData.ASYNCEXCEPTIONMESSAGE,Toast.LENGTH_LONG).show();
            }
            }
            else
            {
                Toast.makeText(Login.this,AppData.ASYNCFAILEDMESSAGE,Toast.LENGTH_LONG).show();
            }
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }//close onPostExecute
    }// close validateUserTask
}
