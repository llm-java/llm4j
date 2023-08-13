package org.llm4j.palm.request;

import com.google.ai.generativelanguage.v1beta2.*;
import org.apache.commons.configuration2.Configuration;
import org.llm4j.palm.PaLMConfig;

import java.util.ArrayList;
import java.util.List;

public class MessageRequestFactory {

    static String CHAT_MODEL_ID = "models/chat-bison-001";

    /**
     * which model to use to generate the result
     */
    private String modelId;
    private PaLMRequestParameters parameters;
    /**
     * E.g. "Respond to all questions with a rhyming poem."
     */
    private String context;
    private List<Message> messageList = new ArrayList<>();
    private List<Example> exampleList = new ArrayList<>();

    public MessageRequestFactory withMessage(String content) {
        messageList.add(Message.newBuilder()
                .setContent(content)
                .build());
        return this;
    }

    public MessageRequestFactory withMessage(String author, String content) {
        Message.Builder builder = Message.newBuilder();
        if(author!=null) builder.setAuthor(author);
        builder.setContent(content);
        messageList.add(builder.build());
        return this;
    }

    public MessageRequestFactory withExample(String text1, String text2) {
        exampleList.add(Example.newBuilder()
                .setInput(Message.newBuilder()
                        .setContent(text1)
                        .build())
                .setOutput(Message.newBuilder()
                        .setContent(text2)
                        .build())
                .build());

        return this;
    }

    public MessageRequestFactory withContext(String context) {
        this.context = context;
        return this;
    }

    public MessageRequestFactory withConfig(Configuration configs) {
        this.modelId = configs.getString(PaLMConfig.MODEL_ID_KEY, CHAT_MODEL_ID);
        this.parameters = PaLMRequestParameters.builder()
                        .withConfig(configs)
                        .build();
        return this;
    }

    public GenerateMessageRequest build() {
        MessagePrompt prompt = MessagePrompt.newBuilder()
                .setContext(context)
                .addAllMessages(messageList)
                .addAllExamples(exampleList)
                .build();
        GenerateMessageRequest request = GenerateMessageRequest.newBuilder()
                .setModel(modelId)
                .setPrompt(prompt)
                .setTemperature(parameters.temperature.floatValue())
                .setCandidateCount(parameters.candidateCount)
                .setTopK(parameters.topK)
                .setTopP(parameters.topP.floatValue())
                .build();
        return request;
    }
}
