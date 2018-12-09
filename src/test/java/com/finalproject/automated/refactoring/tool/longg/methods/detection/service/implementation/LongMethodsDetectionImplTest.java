package com.finalproject.automated.refactoring.tool.longg.methods.detection.service.implementation;

import com.finalproject.automated.refactoring.tool.locs.detection.service.LocsDetection;
import com.finalproject.automated.refactoring.tool.model.MethodModel;
import com.finalproject.automated.refactoring.tool.model.PropertyModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * @author fazazulfikapp
 * @version 1.0.0
 * @since 25 October 2018
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class LongMethodsDetectionImplTest {

    @Autowired
    private LongMethodsDetectionImpl longMethodsDetection;

    @MockBean
    private LocsDetection locsDetection;

    private static final Integer FIRST_INDEX = 0;
    private static final Integer SECOND_INDEX = 1;
    private static final Integer LONG_METHOD_COUNT = 1;
    private static final Integer EMPTY_COUNT = 0;

    private static final Long THRESHOLD = 10L;
    private static final Long FIRST_INDEX_LOC = 2L;
    private static final Long SECOND_INDEX_LOC = 14L;

    private List<MethodModel> methodModels;

    @Before
    public void setUp() {
        methodModels = createMethodModels();

        when(locsDetection.llocDetection(eq(methodModels.get(FIRST_INDEX).getBody())))
                .thenReturn(FIRST_INDEX_LOC);
        when(locsDetection.llocDetection(eq(methodModels.get(SECOND_INDEX).getBody())))
                .thenReturn(SECOND_INDEX_LOC);
    }

    @Test
    public void detect_singleMethod_success() {
        MethodModel methodModel = longMethodsDetection.detect(methodModels.get(SECOND_INDEX), THRESHOLD);
        assertNotNull(methodModel);
        assertEquals(methodModels.get(SECOND_INDEX), methodModel);
    }

    @Test
    public void detect_singleMethod_success_notLongMethod() {
        MethodModel methodModel = longMethodsDetection.detect(methodModels.get(FIRST_INDEX), THRESHOLD);
        assertNull(methodModel);
    }

    @Test
    public void detect_multiMethods_success() {
        List<MethodModel> longMethodModels = longMethodsDetection.detect(methodModels, THRESHOLD);
        assertEquals(LONG_METHOD_COUNT.intValue(), longMethodModels.size());
        assertEquals(methodModels.get(SECOND_INDEX), longMethodModels.get(FIRST_INDEX));
    }

    @Test
    public void detect_multiMethods_success_notLongMethod() {
        methodModels.remove(SECOND_INDEX.intValue());
        List<MethodModel> longMethodModels = longMethodsDetection.detect(methodModels, THRESHOLD);
        assertEquals(EMPTY_COUNT.intValue(), longMethodModels.size());
    }

    @Test(expected = NullPointerException.class)
    public void detect_singleMethod_failed_emptyMethod() {
        MethodModel methodModel = null;
        longMethodsDetection.detect(methodModel, THRESHOLD);
    }

    @Test(expected = NullPointerException.class)
    public void detect_singleMethod_failed_emptyThreshold() {
        longMethodsDetection.detect(methodModels.get(SECOND_INDEX), null);
    }

    @Test(expected = NullPointerException.class)
    public void detect_multiMethods_failed_emptyMethods() {
        methodModels = null;
        longMethodsDetection.detect(methodModels, THRESHOLD);
    }

    @Test(expected = NullPointerException.class)
    public void detect_multiMethods_failed_emptyThreshold() {
        longMethodsDetection.detect(methodModels, null);
    }

    private List<MethodModel> createMethodModels() {
        List<MethodModel> methodModels = new ArrayList<>();

        methodModels.add(MethodModel.builder()
                .keywords(Collections.singletonList("public"))
                .name("EmailHelp")
                .parameters(Arrays.asList(
                        PropertyModel.builder()
                                .type("String")
                                .name("emailSubject")
                                .build(),
                        PropertyModel.builder()
                                .type("String")
                                .name("emailContent")
                                .build()))
                .exceptions(Arrays.asList("Exception", "IOException"))
                .body("\n" +
                        "       mEmailSubject = emailSubject;\n" +
                        "       mEmailContent = emailContent;\n" +
                        "\n")
                .build());

        methodModels.add(MethodModel.builder()
                .keywords(Collections.singletonList("public"))
                .returnType("MyResponse<Integer>")
                .name("addGiftInfoCategory")
                .parameters(Collections.singletonList(
                        PropertyModel.builder()
                                .type("GiftInfoCategory")
                                .name("giftInfoCategory")
                                .build()))
                .body("\n" +
                        "        String message;\n" +
                        "        int response;\n" +
                        "\n" +
                        "        try {\n" +
                        "            giftInfoCategory = mGiftInfoCategoryService.addGiftInfoCategory(giftInfoCategory);\n" +
                        "\n" +
                        "            boolean isSuccess = giftInfoCategory != null;\n" +
                        "            message = isSuccess ? \"Gift info category add success\" : \"Gift info category add failed\";\n" +
                        "            response = isSuccess ? 1 : 0;\n" +
                        "        } catch (DataIntegrityViolationException e) {\n" +
                        "            message = \"Gift info category add failed - Gift info category already exists\";\n" +
                        "            response = 0;\n" +
                        "        } catch (Exception e) {\n" +
                        "            message = \"Gift info category add failed - Internal Server Error\";\n" +
                        "            response = 0;\n" +
                        "        }\n" +
                        "\n" +
                        "        return new MyResponse<>(message, response);\n" +
                        "\n")
                .build());

        return methodModels;
    }
}