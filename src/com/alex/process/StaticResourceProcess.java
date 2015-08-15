package com.alex.process;

import com.alex.request.AlexRequest;
import com.alex.response.AlexResponse;

/**
 * Created by zhangsong on 15/8/15.
 */
public class StaticResourceProcess {
    public void process(AlexRequest alexRequest,AlexResponse alexResponse){
        alexResponse.sendStaticResource();
    }

}
