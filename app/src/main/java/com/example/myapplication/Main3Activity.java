package com.example.myapplication;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.security.PrivateKey;

public class Main3Activity extends AppCompatActivity {


    private Button button ;
    private Button button_sel ;
    private EditText editText;
    private Button bu_send;
    private String file_path="";
private TextView textView;
private ProgressBar progressBar;
    int n;


    private static final int FILE_SELECT_CODE = 0;
    private static final String TAG = "VideoActivity";
    private void chooseFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "选择文件"), FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "亲，木有文件管理器啊-_-!!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if (resultCode != Activity.RESULT_OK) {
            Log.e(TAG, "onActivityResult() error, resultCode: " + resultCode);
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }
        if (requestCode == FILE_SELECT_CODE) {
            Uri uri = data.getData();
            Log.i(TAG, "------->" + uri.getPath());
            file_path=getRealFilePath(this,uri);
         file_path=file_path.replace("/root","");
          //  file_path=uri.getPath().replace("/root","");
          // file_path=getRealFilePath(this,uri);
            textView.setText("the file you select:"+file_path);

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public static String getRealFilePath( final Context context, final Uri uri ) {
        if ( null == uri ) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if ( scheme == null )
            data = uri.getPath();
        else if ( ContentResolver.SCHEME_FILE.equals( scheme ) ) {
            data = uri.getPath();
        } else if ( ContentResolver.SCHEME_CONTENT.equals( scheme ) ) {
            Cursor cursor = context.getContentResolver().query( uri, new String[] { MediaStore.Images.ImageColumns.DATA }, null, null, null );
            if ( null != cursor ) {
                if ( cursor.moveToFirst() ) {
                    int index = cursor.getColumnIndex( MediaStore.Images.ImageColumns.DATA );
                    if ( index > -1 ) {
                        data = cursor.getString( index );
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String column = MediaStore.Images.Media.DATA;
        String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return null;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_PROGRESS);

        setContentView(R.layout.activity_main3);
        button = findViewById(R.id.button4);
        button_sel = findViewById(R.id.button_sel);
        editText = findViewById(R.id.editText3);
        bu_send = findViewById(R.id.but_send);
        textView=findViewById(R.id.text_mess);
        progressBar=findViewById(R.id.progressBar);
        progressBar.setProgress(0);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent a = new Intent();
                a.setClass(Main3Activity.this, MainActivity.class);
                startActivity(a);
            }
        });
        button_sel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
     chooseFile();
            }
        });
        bu_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                progressBar.setProgress(0);
              final   File file=new File(file_path);
                if(file.exists()) {
                    final String filename = file.getName();
                final  String ip=editText.getText().toString();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            SocketChannel socketChannel = null;
                            try {

                                socketChannel = SocketChannel.open();
                                SocketAddress socketAddress = new InetSocketAddress(ip, 1991);
                                socketChannel.connect(socketAddress);
                                sendData(socketChannel, "2;"+filename);
                                String string = "";
                                string = receiveData(socketChannel);
                                if (!string.isEmpty())
                                {
                                    socketChannel = SocketChannel.open();
                                    socketAddress = new InetSocketAddress(ip, 2666);
                                    socketChannel.connect(socketAddress);

                                    sendFile(socketChannel,file);

                                }}
                            catch (Exception e)
                            {
                                Log.e("Warning",e.toString());
                            }
                        }
                    }).start();}
                else
                {
                    Log.e("file","not exist!");
                }



            }
        });

    }







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

    private  void sendFile(SocketChannel socketChannel, File file) throws IOException {

        int time=(int)file.length();
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


        FileInputStream fis = null;
        FileChannel channel = null;
        try {
            fis = new FileInputStream(file);
            channel = fis.getChannel();
            ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
            int size = 0;

            while ((size = channel.read(buffer)) != -1) {
                if(rate<1) {
                    progressBar.setProgress((int) (rate * 100));
                    rate = rate1 * n;
                    n = n + 1;
                }


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
                progressBar.setProgress(100);
                n=1;
                channel.close();
            } catch(Exception ex) {}
            try {
                fis.close();
            } catch(Exception ex) {}
        }
    }

    private  void receiveFile(SocketChannel socketChannel, File file) throws IOException {
        Log.d("receiveFile","receiverFile");
        FileOutputStream fos = null;
        FileChannel channel = null;

        try {
            fos = new FileOutputStream(file);
            channel = fos.getChannel();
            ByteBuffer buffer = ByteBuffer.allocateDirect(1024);


            int size = 0;
            while ((size = socketChannel.read(buffer)) != -1) {
                buffer.flip();
                if (size > 0) {
                    buffer.limit(size);
                    channel.write(buffer);
                    buffer.clear();
                }


            }
            textView.setText("File send complete");
        }catch (Exception e){
            channel.close();
            fos.close();
            Log.e("","here"+e.toString());
        }
        finally {
            channel.close();
            fos.close();
        }
    }

}
