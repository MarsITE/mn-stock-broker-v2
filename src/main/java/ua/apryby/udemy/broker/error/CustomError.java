package ua.apryby.udemy.broker.error;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomError {
    private int status;
    private String error;
    private String message;
    private String path;
}
