package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

public class Main2Activity extends AppCompatActivity {
private Button butt_1;
private Button butt_2;
private Button but_re;
private TextView text_1;
private TextView text_2;
private TextView text_3;
private static double time;
private ProgressBar progressBar;
private static String path_glo="/storage/emulated/0";
int n;

    public static boolean check(String str,String a)
    {

        int result1 = str.indexOf(a);
        if(result1 != -1){
            System.out.println("字符串str中包含子串:"+result1);
            return true;
        }
        else
        {
            return false;
        }
    }
    public static String[] trim(String character, String symbol){
        //
        return character.split(symbol);
    }

        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_PROGRESS);
           // requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.activity_main2);
butt_1=findViewById(R.id.butt_1);//按钮
text_1=findViewById(R.id.editText);
text_2=findViewById(R.id.editText2);
text_3=findViewById(R.id.test_storage);
but_re=findViewById(R.id.button_re);
progressBar=findViewById(R.id.bar_1);
progressBar.setProgress(0);

        butt_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
progressBar.setProgress(0);
               final String ip;
                    // TODO Auto-generated method stub
                   ip = text_1.getText().toString();
                   final File file=new File(path_glo+text_2.getText().toString());
                   if(!file.exists()) {
                       try {
                           file.createNewFile();
                       } catch (IOException e) {
                           e.printStackTrace();
                       }
                   }
                   Log.d(file.getAbsolutePath(),"!!!!!!!!!");
                    new Thread(new Runnable() {
                       @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            SocketChannel socketChannel = null;
                            try {
                                socketChannel = SocketChannel.open();
                                SocketAddress socketAddress = new InetSocketAddress(ip, 1991);
                                socketChannel.connect(socketAddress);

                                sendData(socketChannel, "1;"+text_2.getText().toString());
                                String string = "";
                                string = receiveData(socketChannel);
                                if(!string.isEmpty()){

                                    if(check(string,";"))
                                    {

                                        String [] str=trim(string,";");
                                        string=str[1];time=Double.valueOf(str[0]);
                                        Log.d("!!!!",String.valueOf(time));
                                        socketChannel = SocketChannel.open();
                                        socketChannel.connect(new InetSocketAddress(ip, 1991));
                                        sendData(socketChannel, "3;"+string);
                                        Log.d("!!!!!!!",string);
                                        receiveFile(socketChannel,file);
                                        text_3.setText("  file receive complete please check");
                                        //      Toast.makeText(getApplicationContext(),"file receive complete please check:"+path_glo,Toast.LENGTH_SHORT).show();
                                        Log.d("!!!!!!!",path_glo+string);

                                    }

                                }
                            } catch (Exception ex) {
                                Log.i("chz", null, ex);
                            } finally {
                                try {
                                    socketChannel.close();
                                } catch(Exception ex) {}
                            }
                        }
                    }).start();
                }
            });

but_re.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        Intent a=new Intent();
        a.setClass(Main2Activity.this,MainActivity.class);
        startActivity(a);
    }
});
filecreate();
    }


    private void filecreate()
    {

        String path = "";
        Boolean flag = false;
        File file;
        path=Environment.getExternalStorageDirectory().getPath();
        Log.e("___________________________------------",path+"/runtest");

        path_glo=path+"/runtest/";
        text_3.setText("The file you wanna receive will be put into:\n :"+path_glo);

        file=new File(path+"/runtest");

        if (!file.exists())
        {


            Log.d("!!!","!!!!!!!");
            file.mkdir();
                if(file.exists())
                {
                    Log.d("OK","OK");
                    if (file.exists())
                    {
                        text_3.setText("The file you wanna receive will be put into:/runtest"+"\n");
                        Log.d("OK","OKOKOKOKOKO");
                        Log.d("!!!!!!!!",file.getAbsoluteFile().toString());

                    }


                }
             else
                {
                    text_3.setText("file create fail!");
                    Log.d("FailFail","OK");
                }

            }
            else{
            text_3.setText("The file you wanna receive will be put into:\n :"+path_glo);
        }
        }


    /*
         path = Environment.getExternalStorageDirectory().toString + "/xxx(文件夹)";
        file = new File(path);
        if(file.exists()){
            //创建文件夹
            file.mkdirs();
        }else{
            path = path  + "xxx(要保存的文件)"
        }
        file = new File(path);
        if(file.exists){
            //创建文件
            file.createNewFile();
        }
        Writer write = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
        write.write(jsonObject.toString());
        write.flush();
        write.close();
        file = null;
*/


    private void sendData(SocketChannel socketChannel, String string) throws IOException {
        byte[] bytes = string.getBytes();
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        socketChannel.write(buffer);
        socketChannel.socket().shutdownOutput();
    }

    private String receiveData(SocketChannel socketChannel) throws IOException {
        String string = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
            byte[] bytes;
            int count = 0;
            while ((count = socketChannel.read(buffer)) >= 0) {
                buffer.flip();
                bytes = new byte[count];
                buffer.get(bytes);
                baos.write(bytes);
                buffer.clear();

            }
            bytes = baos.toByteArray();
            string = new String(bytes);
//				socketChannel.socket().shutdownInput();
        } finally {
            try {
                baos.close();
            } catch(Exception ex) {}
        }
        return string;
    }

    private static void sendFile(SocketChannel socketChannel, File file) throws IOException {
        FileInputStream fis = null;
        FileChannel channel = null;

        try {
            fis = new FileInputStream(file);
            channel = fis.getChannel();
            ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
            int size = 0;

            while ((size = channel.read(buffer)) != -1) {
                System.out.println(size);
                buffer.rewind();
                buffer.limit(size);
                socketChannel.write(buffer);
                buffer.clear();
            }
            socketChannel.socket().shutdownOutput();

        } finally {
            try {
                //	socketChannel.close();
                channel.close();
            } catch(Exception ex) {}
            try {
                fis.close();
            } catch(Exception ex) {}
        }
    }

    private void receiveFile(SocketChannel socketChannel, File file) throws IOException {
        Log.d("receiveFile","receiverFile");

        double rate=0.0;
        double rate1=0.0;
       int gate;
       int htime=0;
      n=1;
        if(time!=0)
        {
            gate=(int)(time/1024)+1;
            rate=1.0/gate;
           rate1=rate;
            Log.d("______",String.valueOf(gate)+"   "+String.valueOf(rate));
        }
        FileOutputStream fos = null;
        FileChannel channel = null;


        try {
            fos = new FileOutputStream(file);
            channel = fos.getChannel();
            ByteBuffer buffer = ByteBuffer.allocateDirect(1024);


            int size = 0;
            while ((size = socketChannel.read(buffer)) != -1) {
              if(rate<1) {
                  text_3.setText("  file trans rate:" +rate);
                    progressBar.setProgress((int) (rate * 100));
                    rate = rate1* n;
                    n = n + 1;
Log.d("rate:",String.valueOf(rate));
                }

             buffer.flip();
                if (size > 0) {


                    buffer.limit(size);
                    channel.write(buffer);
                    buffer.clear();
                }
           }

        }catch (Exception e){

            channel.close();
            fos.close();
            Log.e("","here"+e.toString());
        }
        finally {
            text_3.setText("file fetch complete");
            progressBar.setProgress(100);
            n=1;
            channel.close();
            fos.close();
        }
    }




}
