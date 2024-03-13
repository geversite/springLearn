package org.myTomcat.http;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.BufferedReader;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HttpResponse {

    int code;
    String msg;

}
