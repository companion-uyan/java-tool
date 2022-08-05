package person.companion.ddns;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Title:
 * Author companion
 * Written by: 2022/4/5 22:10
 * Describe:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DomainRecord {
    private String rr;

    private String value;

    private String recordId;
}
