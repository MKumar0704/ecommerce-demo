package com.ecom.productservice.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductRequestDto {

    @NotBlank(message = "Product name is required")
    private String name;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Price is req")
    @Pattern(regexp = "\\d+(\\.\\d{1,2})?", message = "Price must be a valid decimal number")
    private String price;

    @NotBlank(message = "Stock is required")
    @Pattern(regexp = "\\d+", message = "Stock must be a valid non negative integer")
    private String stock;

    public @NotBlank(message = "Category name is required") String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(@NotBlank(message = "Category name is required") String categoryName) {
        this.categoryName = categoryName;
    }

    @NotBlank(message = "Category name is required")
    private String categoryName;

}
