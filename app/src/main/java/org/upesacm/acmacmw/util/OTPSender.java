package org.upesacm.acmacmw.util;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.upesacm.acmacmw.util.mail.GMailSender;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class OTPSender extends AsyncTask<String, Void, String> {
    private String subject = "ACM Mail";
    private String mailBody;
    private String recipientMail;
    private final static String ACM_EMAIL = "appdev.upesacmacmw@gmail.com";               //ACM's gmail address
    private final static String ACM_PASSWORD = "appdev2018-2019";                      //ACM's gmsil sccount'd password
    @Override
    protected String doInBackground(String... params) {
        mailBody=params[0];
        recipientMail=params[1];
        subject = params[2];
        try {

            GMailSender sender = new GMailSender(ACM_EMAIL, ACM_PASSWORD);          //Constructor call to LogIn
            boolean mailSent = sender.sendMail(subject,mailBody,ACM_EMAIL,   //Include Subject, body, Sender's gmail and recipient's email
                    recipientMail);
            if(!mailSent) {
                return sendMailThroughPHPMailer(recipientMail,subject,mailBody);
            }
            else {
                return "Mail sent through default gateway";
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return sendMailThroughPHPMailer(recipientMail, subject, mailBody);
        }
    }


    String sendMailThroughPHPMailer(String recipientMail,String subject,String mailBody) {
        System.out.println("Trying to send mail using PHP");
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://upesacm.org/upesacmacmwapp/mail_sender.php?pass=appdev2018-2019&address=" +
                        recipientMail+"&subject="+subject+"&message="+mailBody)
                .build();

        try {
            Response response = client.newCall(request).execute();
            return response.body().string();
        }catch (Exception e) {
            e.printStackTrace();
            return "Mail sending failed";
        }
    }

    @Override
    protected void onPostExecute(String result) {
        Log.e("OTPSender",result+"");


    }

}