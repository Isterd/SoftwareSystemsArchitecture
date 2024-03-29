package com.example.architecture.messages;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ExperimentParamsRequest {
    @NonNull
    int sourceNum;
    @NonNull
    int bufferNum;
    @NonNull
    int deviceNum;
    @NonNull
    int eventsNum;
    @NonNull
    double alpha;
    @NonNull
    double beta;
    @NonNull
    double lambda;
}
