package com.bit.datainkback.entity;

import com.bit.datainkback.dto.LabelFieldDto;
import jakarta.persistence.*;
import lombok.*;

@Entity
@SequenceGenerator(
        name = "labelFieldSeqGenerator",
        sequenceName = "LABELFIELD_SEQ",
        initialValue = 1,
        allocationSize = 1
)
@Table(name = "LABEL_FIELD")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LabelField {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "labelFieldSeqGenerator"
    )
    @Column(name = "field_id")
    private Long fieldId;

    @Column(nullable = false)
    private String name;

    @Column(name = "mongo_data_id", nullable = false)
    private String mongoDataId;

    public LabelFieldDto toDto() {
        return LabelFieldDto.builder()
                .fieldId(this.fieldId)
                .name(this.name)
                .mongoDataId(this.mongoDataId)
                .build();
    }
}
