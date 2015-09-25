package com.qrcode;


import com.google.zxing.WriterException;
import com.zxing.activity.CaptureActivity;
import com.zxing.encoding.EncodingHandler;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private TextView resultTextView;
	private EditText qrStrEditText;
	private ImageView qrImgImageView;
    private Button btnDown;
    private static  Handler handler=new Handler();
    private TextView mTextView1;
    private static String uriAPI    = "http://wap.easou.com/";
    private   String strResult;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        btnDown = (Button) findViewById(R.id.btnDown);
        mTextView1=(TextView)findViewById(R.id.textView);
        resultTextView = (TextView) this.findViewById(R.id.tv_scan_result);
        qrStrEditText = (EditText) this.findViewById(R.id.et_qr_string);
        qrImgImageView = (ImageView) this.findViewById(R.id.iv_qr_image);
        
        Button scanBarCodeButton = (Button) this.findViewById(R.id.btn_scan_barcode);
        scanBarCodeButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
					
				Intent openCameraIntent = new Intent(MainActivity.this,CaptureActivity.class);
				startActivityForResult(openCameraIntent, 0);
			}
		});
        btnDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 开启一个子线程，用于下载 网页 xml
                new Thread(new MyThread()).start();
                // 显示对话框
                //       dialog.show();
            }
        });


        Button generateQRCodeButton = (Button) this.findViewById(R.id.btn_add_qrcode);
        generateQRCodeButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try {
					String contentString = qrStrEditText.getText().toString();
					if (!contentString.equals("")) {
						
						Bitmap qrCodeBitmap = EncodingHandler.createQRCode(contentString, 350);
						qrImgImageView.setImageBitmap(qrCodeBitmap);
					}else {
						Toast.makeText(MainActivity.this, "Text can not be empty", Toast.LENGTH_SHORT).show();
					}
					
				} catch (WriterException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});



    }

    public class MyThread implements Runnable {

        @Override
        public void run() {
            // 下载xml
            try {
                //***************************************************************
                //   httpResponse = httpClient.execute(httpGet);
                // if (httpResponse.getStatusLine().getStatusCode() == 200) {
                //   byte[] data = EntityUtils.toByteArray(httpResponse
                //         .getEntity());
                // 得到一个Bitmap对象，并且为了使其在post内部可以访问，必须声明为final
                //final Bitmap bmp=BitmapFactory.decodeByteArray(data, 0, data.length);
                 uriAPI=resultTextView.getText().toString();
                HttpPost httpRequest = new HttpPost(uriAPI);
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("str", "post string"));


                httpRequest.setEntity(new UrlEncodedFormEntity(params,
                        HTTP.UTF_8));
                HttpResponse httpResponse = new DefaultHttpClient()
                        .execute(httpRequest);

                if (httpResponse.getStatusLine().getStatusCode() == 200) {

                    strResult = EntityUtils.toString(httpResponse
                            .getEntity());
                    // mTextView1.setText(strResult);
                } else {
                    // mTextView1.setText("Error Response: "+httpResponse.getStatusLine().toString());
                }

                /*catch (ClientProtocolException e) {
                    mTextView1.setText(e.getMessage().toString());
                    e.printStackTrace();
                } catch (IOException e) {
                    mTextView1.setText(e.getMessage().toString());
                    e.printStackTrace();
                } catch (Exception e) {
                    mTextView1.setText(e.getMessage().toString());
                    e.printStackTrace();
                }*/


                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        // 在Post中操作UI组件mTextView
                       // btnDown.setText("nihao");
                        mTextView1.setText(strResult);
                    }
                });
                // 隐藏对话框
                //         dialog.dismiss();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	
		if (resultCode == RESULT_OK) {
			Bundle bundle = data.getExtras();
			String scanResult = bundle.getString("result");
			resultTextView.setText(scanResult);
		}
	}
}