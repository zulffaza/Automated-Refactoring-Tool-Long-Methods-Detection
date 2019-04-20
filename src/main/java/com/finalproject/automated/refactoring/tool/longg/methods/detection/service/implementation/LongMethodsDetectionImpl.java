package com.finalproject.automated.refactoring.tool.longg.methods.detection.service.implementation;

import com.finalproject.automated.refactoring.tool.locs.detection.service.LocsDetection;
import com.finalproject.automated.refactoring.tool.longg.methods.detection.service.LongMethodsDetection;
import com.finalproject.automated.refactoring.tool.model.CodeSmellName;
import com.finalproject.automated.refactoring.tool.model.MethodModel;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * @author fazazulfikapp
 * @version 1.0.0
 * @since 25 October 2018
 */

@Service
public class LongMethodsDetectionImpl implements LongMethodsDetection {

    @Autowired
    private LocsDetection locsDetection;

    @Override
    public void detect(@NonNull MethodModel methodModel, @NonNull Long threshold) {
        detect(Collections.singletonList(methodModel), threshold);
    }

    @Override
    public void detect(@NonNull List<MethodModel> methodModels, @NonNull Long threshold) {
        methodModels.parallelStream()
                .map(this::detectLoc)
                .filter(methodModel -> isLongMethod(methodModel, threshold))
                .forEach(this::checkMethod);
    }

    private MethodModel detectLoc(MethodModel methodModel) {
        Long loc = locsDetection.llocDetection(methodModel.getBody());
        methodModel.setLoc(loc);

        return methodModel;
    }

    private Boolean isLongMethod(MethodModel methodModel, Long threshold) {
        return methodModel.getLoc() > threshold;
    }

    private void checkMethod(MethodModel methodModel) {
        methodModel.getCodeSmells()
                .add(CodeSmellName.LONG_METHOD);
    }
}
