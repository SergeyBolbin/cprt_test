package org.sbolbin.crpt.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    @Size(min=9, max=9)
    String seller;

    @Size(min=9, max=9)
    String customer;

    @NotEmpty
    List<@Valid Product> products;
}
