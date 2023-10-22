package com.training.social_media.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PostDto {

    private static final String HEADER_FIELD_IS_EMPTY = "Header field shouldn't be empty";
    private static final String WRONG_SIZE_OF_HEADER = "Header shouldn't be more than 15 symbols";
    private static final String TEXT_FIELD_IS_EMPTY = "Text field shouldn't be empty";
    private static final String WRONG_SIZE_OF_TEXT = "Text shouldn't be more than 100 symbols";

    private Long id;

    @NotBlank(message = HEADER_FIELD_IS_EMPTY)
    @Size(max = 15, message = WRONG_SIZE_OF_HEADER)
    private String header;

    @NotBlank(message = TEXT_FIELD_IS_EMPTY)
    @Size(max = 100, message = WRONG_SIZE_OF_TEXT)
    private String text;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime date;

    private String user;
}
