package fcu.selab.progedu.jenkinsconfig;

import org.junit.jupiter.api.Test;
import org.apache.commons.lang3.StringEscapeUtils;

class AndroidPipelineConfigTest {
    AndroidPipelineConfig androidPipelineConfig1 = new AndroidPipelineConfig("franky", "franky",
            "franky", "franky","Compile Failure:10, Unit Test Failure:20, Coding Style Failure:30");

    AndroidPipelineConfig androidPipelineConfig2 = new AndroidPipelineConfig("franky", "franky",
            "franky", "franky");

    @Test
    public void createPipeline() {
        System.out.println(androidPipelineConfig2.getXmlConfig());
    }

    @Test
    public void createStage() {
        String name = "compile";
        String command = "./gradlew compileDebugJavaWithJavac";
        String[] commands = {"chmod +x gradlew", "./gradlew compileDebugJavaWithJavac"};
        String result1 = androidPipelineConfig1.makeStageString(name, command);
        String result2 = androidPipelineConfig1.makeStageString(name, commands);
        System.out.println(result1);
        System.out.println("=========================================================");
        System.out.println(result2);
    }

    @Test
    public void createCommand() {
        String result = androidPipelineConfig1.getDockerCommand("Unit Test Failure");
        System.out.println(result);
    }

    @Test
    public void testEscape() {
        String test = StringEscapeUtils.escapeJava("$");
        System.out.println(test);
    }
}