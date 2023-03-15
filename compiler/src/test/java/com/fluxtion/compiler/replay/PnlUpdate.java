package com.fluxtion.compiler.replay;

import com.fluxtion.runtime.event.Event;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PnlUpdate implements Event {
    String bookName;
    int amount;

    @Override
    public String filterString() {
        return bookName;
    }
}
