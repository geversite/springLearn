package org.myTomcat.http;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtil {

    public static HttpResponse doHttpGet(URL url){
        BufferedReader reader = null;
        HttpURLConnection connection = null;

        try {
            connection = (HttpURLConnection) url.openConnection();
            // 设置请求方法为GET
            connection.setRequestMethod("GET");
            // 连接到服务器
            connection.connect();

            int responseCode = connection.getResponseCode();
            InputStream inputStream;
            // 根据响应代码决定获取输入流还是错误流
            if (responseCode == HttpURLConnection.HTTP_OK) {
                inputStream = connection.getInputStream();
            } else {
                inputStream = connection.getErrorStream();
            }

            // 创建输入流读取器
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            StringBuilder response = new StringBuilder();

            // 读取输入流
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return new HttpResponse(responseCode, response.toString());
        }catch (Exception e){
            throw new RuntimeException();
        }finally {
            try {
                if(connection!=null){
                    connection.disconnect();
                }
                if(reader!=null){
                    reader.close();
                }
            }catch (Exception e){
                throw new RuntimeException();
            }
        }
    }


    public static HttpResponse doHttpPost(URL url, String requestBody) {
        HttpURLConnection connection = null;
        OutputStream os = null;
        BufferedReader reader = null;
        HttpResponse response = new HttpResponse();
        try {

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // 写入请求体
            os = connection.getOutputStream();
            os.write(requestBody.getBytes());
            os.flush();

            // 获取响应码
            response.code = connection.getResponseCode();

            // 根据响应码获取输入流或错误流
            InputStream inputStream = (response.code == HttpURLConnection.HTTP_OK) ?
                    connection.getInputStream() : connection.getErrorStream();

            // 读取响应内容
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            StringBuilder responseBody = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                responseBody.append(line);
            }
            response.msg = responseBody.toString();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭资源
            try {
                if (os != null) os.close();
                if (reader != null) reader.close();
                if (connection != null) connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return response;
    }

}
