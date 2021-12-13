package com.example.hmdfpm;

import java.io.Serializable;

public class DTO implements Serializable {

    int dLength;
    int dRow;
    int dCol;
    int dType;
    byte[] dBuffer;
    KeyPointDTO[] kp;
    
}
