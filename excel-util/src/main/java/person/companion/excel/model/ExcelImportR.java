package person.companion.excel.model;

import com.google.gson.JsonObject;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Title:
 * Author: companion
 * Written by: 2022/8/4 10:47
 */
@Data
@NoArgsConstructor
public class ExcelImportR<E> {
    private List<E> data;

    private List<JsonObject> errorData;
}
