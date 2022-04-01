package org.sbolbin.crpt.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    @Size(min=13, max=13, message = "product code must contain exactly 13 symbols")
    @NotNull(message = "product code must be specified")
    String code;

    @NotBlank(message = "product name must be specified")
    String name;
}
