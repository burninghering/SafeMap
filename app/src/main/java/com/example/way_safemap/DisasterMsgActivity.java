package com.example.way_safemap;

import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.net.URL;

public class DisasterMsgActivity extends AppCompatActivity {

    TextView scrollableText;
    EditText editText;
    ScrollView textViewWrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disaster_msg);
        scrollableText = (TextView) findViewById(R.id.result);
        editText = (EditText) findViewById(R.id.editText);
        textViewWrapper = (ScrollView) findViewById(R.id.textViewWrapper);
        Button button = (Button) findViewById(R.id.button);
        scrollableText.setText(R.string.longText);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String criteria = editText.getText().toString();
                String fullText = scrollableText.getText().toString();
                if (fullText.contains(criteria)) {
                    int indexOfCriteria = fullText.indexOf(criteria);
                    int lineNumber = scrollableText.getLayout().getLineForOffset(indexOfCriteria);
                    String highlighted = "<font color='red'>"+criteria+"</font>";
                    fullText = fullText.replace(criteria, highlighted);
//                    scrollableText.setText(Html.fromHtml(fullText));

                    textViewWrapper.scrollTo(0, scrollableText.getLayout().getLineTop(lineNumber));
                }
            }
        });

        StrictMode.enableDefaults();
        TextView status1 = (TextView) findViewById(R.id.result); //파싱된 결과확인!

        boolean inrow = false, inDate = false, inId = false, inLoName = false, inSn = false;
        boolean inMsg = false, inPlat = false;

        String create_date = null, location_id = null, location_name = null, md101_sn = null, msg = null, send_platform = null;


        try {
            URL url = new URL("http://apis.data.go.kr/1741000/DisasterMsg2/getDisasterMsgList?"
                    + "&pageNo=1&numOfRows=50&ServiceKey="
                    + "OFYdZ82UOWf%2BPgfK%2F9uXylhXOpjUQ582YmkF8PQXHX%2F0VPlSTmtQKNXmY3HeAIxznpkfC%2BzGCCY%2BL4ip%2Bc%2Fmow%3D%3D"
            ); //검색 URL부분

            XmlPullParserFactory parserCreator = XmlPullParserFactory.newInstance();
            XmlPullParser parser = parserCreator.newPullParser();

            parser.setInput(url.openStream(), null);

            int parserEvent = parser.getEventType();
            System.out.println("파싱시작합니다.");

            while (parserEvent != XmlPullParser.END_DOCUMENT) {
                switch (parserEvent) {
                    case XmlPullParser.START_TAG://parser가 시작 태그를 만나면 실행
                        if (parser.getName().equals("create_date")) { //title 만나면 내용을 받을수 있게 하자
                            inDate = true;
                        }
                        if (parser.getName().equals("location_id")) { //address 만나면 내용을 받을수 있게 하자
                            inId = true;
                        }
                        if (parser.getName().equals("location_name")) { //mapx 만나면 내용을 받을수 있게 하자
                            inLoName = true;
                        }
                        if (parser.getName().equals("md101_sn")) { //mapy 만나면 내용을 받을수 있게 하자
                            inSn = true;
                        }
                        if (parser.getName().equals("msg")) { //mapy 만나면 내용을 받을수 있게 하자
                            inMsg = true;
                        }
                        if (parser.getName().equals("send_platform")) { //mapy 만나면 내용을 받을수 있게 하자
                            inPlat = true;
                        }
                        if (parser.getName().equals("message")) { //message 태그를 만나면 에러 출력
                            status1.setText(status1.getText() + "에러");
                            //여기에 에러코드에 따라 다른 메세지를 출력하도록 할 수 있다.
                        }
                        break;

                    case XmlPullParser.TEXT://parser가 내용에 접근했을때
                        if (inDate) { //isTitle이 true일 때 태그의 내용을 저장.
                            create_date = parser.getText();
                            inDate = false;
                        }
                        if (inId) { //isAddress이 true일 때 태그의 내용을 저장.
                            location_id = parser.getText();
                            inId = false;
                        }
                        if (inLoName) { //isMapx이 true일 때 태그의 내용을 저장.
                            location_name = parser.getText();
                            inLoName = false;
                        }
                        if (inSn) { //isMapy이 true일 때 태그의 내용을 저장.
                            md101_sn = parser.getText();
                            inSn = false;
                        }
                        if (inMsg) { //isMapy이 true일 때 태그의 내용을 저장.
                            msg = parser.getText();
                            inMsg = false;
                        }
                        if (inPlat) { //isMapy이 true일 때 태그의 내용을 저장.
                            send_platform = parser.getText();
                            inPlat = false;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (parser.getName().equals("row")) {
                            status1.setText(status1.getText() + "수신 번호 : " + md101_sn + "\n 날짜 : " + create_date + "\n 지역명 : " + location_name
                                    + "\n 지역 번호: " + location_id + "\n 재난 문자 : \n" + msg + "\n\n"
                            );
                            inrow = false;
                        }
                        break;
                }
                parserEvent = parser.next();
            }
        } catch (Exception e) {
            status1.setText("에러가..났습니다...");
        }
    }

}