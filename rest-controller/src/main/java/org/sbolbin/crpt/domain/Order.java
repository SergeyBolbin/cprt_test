package org.sbolbin.crpt.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    @Size(min=9, max=9, message = "seller must contain exactly 9 symbols")
    @NotNull(message = "seller must be specified")
    String seller;

    @Size(min=9, max=9, message = "customer must contain exactly 9 symbols")
    @NotNull(message = "customer must be specified")
    String customer;

    @NotEmpty
    List<@Valid Product> products;
}
