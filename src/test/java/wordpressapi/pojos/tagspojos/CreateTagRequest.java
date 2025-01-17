package wordpressapi.pojos.tagspojos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateTagRequest {
    private String name;
    private String description;
}