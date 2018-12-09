package com.finalproject.automated.refactoring.tool.longg.methods.detection.service.implementation;

import com.finalproject.automated.refactoring.tool.locs.detection.service.LocsDetection;
import com.finalproject.automated.refactoring.tool.longg.methods.detection.service.LongMethodsDetection;
import com.finalproject.automated.refactoring.tool.model.MethodModel;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author fazazulfikapp
 * @version 1.0.0
 * @since 25 October 2018
 */

@Service
public class LongMethodsDetectionImpl implements LongMethodsDetection {

    @Autowired
    private LocsDetection locsDetection;

    private static final Integer FIRST_INDEX = 0;

    @Override
    public MethodModel detect(@NonNull MethodModel methodModel, @NonNull Long threshold) {
        try {
            return detect(Collections.singletonList(methodModel), threshold)
                    .get(FIRST_INDEX);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    @Override
    public List<MethodModel> detect(@NonNull List<MethodModel> methodModels, @NonNull Long threshold) {
        return methodModels.stream()
                .map(this::detectLoc)
                .filter(methodModel -> isLongMethod(methodModel, threshold))
                .collect(Collectors.toList());
    }

    private MethodModel detectLoc(MethodModel methodModel) {
        Long loc = locsDetection.llocDetection(methodModel.getBody());
        methodModel.setLoc(loc);

        return methodModel;
    }

    private Boolean isLongMethod(MethodModel methodModel, Long threshold) {
        return methodModel.getLoc() > threshold;
    }
}
