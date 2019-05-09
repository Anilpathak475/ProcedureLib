package com.github.phonestate.speedtest;

import android.os.AsyncTask;
import androidx.lifecycle.MutableLiveData;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author erdigurbuz
 */
public class HttpDownloadTest extends AsyncTask<Void, Void, Void> {

    public String fileURL = "";
    MutableLiveData<Double> finalDownloadRate = new MutableLiveData<>();
    private long startTime;
    private long endTime;
    private int downloadedByte;
    private boolean finished = false;
    private double instantDownloadRate = 0;
    private HttpURLConnection httpConn = null;
    private double downloadElapsedTime = 0;

    public HttpDownloadTest() {
        endTime = 0;
        startTime = 0;
        downloadedByte = 0;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        URL url = null;
        downloadedByte = 0;
        int responseCode = 0;

        List<String> fileUrls = new ArrayList<>();
        fileUrls.add(fileURL + "random4000x4000.jpg");
        fileUrls.add(fileURL + "random3000x3000.jpg");

        startTime = System.currentTimeMillis();

        outer:
        for (String link : fileUrls) {
            try {
                url = new URL(link);
                httpConn = (HttpURLConnection) url.openConnection();
                responseCode = httpConn.getResponseCode();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            try {
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    byte[] buffer = new byte[10240];

                    InputStream inputStream = httpConn.getInputStream();
                    int len = 0;

                    while ((len = inputStream.read(buffer)) != -1) {
                        downloadedByte += len;
                        endTime = System.currentTimeMillis();
                        downloadElapsedTime = (endTime - startTime) / 1000.0;
                        setInstantDownloadRate(downloadedByte, downloadElapsedTime);
                        int timeout = 15;
                        if (downloadElapsedTime >= timeout) {
                            break outer;
                        }
                    }

                    inputStream.close();
                    httpConn.disconnect();
                } else {
                    System.out.println("Link not found...");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }


        finished = true;
        return null;
    }

    @Override
    protected void onPostExecute(Void integer) {
        super.onPostExecute(integer);
        endTime = System.currentTimeMillis();
        downloadElapsedTime = (endTime - startTime) / 1000.0;
        finalDownloadRate.setValue(((downloadedByte * 8) / (1000 * 1000.0)) / downloadElapsedTime);
    }

    private double round(double value) {
        BigDecimal bd;
        try {
            bd = new BigDecimal(value);
        } catch (Exception ex) {
            return 0.0;
        }
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public double getInstantDownloadRate() {
        return instantDownloadRate;
    }

    private void setInstantDownloadRate(int downloadedByte, double elapsedTime) {

        if (downloadedByte >= 0) {
            this.instantDownloadRate = round((((downloadedByte * 8) / (1000 * 1000)) / elapsedTime));
        } else {
            this.instantDownloadRate = 0.0;
        }
    }

    public MutableLiveData<Double> getFinalDownloadRate() {
        return finalDownloadRate;
    }

    public boolean isFinished() {
        return finished;
    }
}
