package com.thor.prediction.model;

import lombok.Builder;
import lombok.Data;

/**
 * @author huangpin
 * @date 2019-06-17
 */
@Data
@Builder
public class DataModel {

    private Integer index;

    private Integer queryNum;

    private double diff;
}
