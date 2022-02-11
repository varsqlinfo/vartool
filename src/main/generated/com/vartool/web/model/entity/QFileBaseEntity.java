package com.vartool.web.model.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QFileBaseEntity is a Querydsl query type for FileBaseEntity
 */
@Generated("com.querydsl.codegen.SupertypeSerializer")
public class QFileBaseEntity extends EntityPathBase<FileBaseEntity> {

    private static final long serialVersionUID = 866982272L;

    public static final QFileBaseEntity fileBaseEntity = new QFileBaseEntity("fileBaseEntity");

    public final com.vartool.web.model.entity.base.QAbstractRegAuditorModel _super = new com.vartool.web.model.entity.base.QAbstractRegAuditorModel(this);

    public final StringPath fileExt = createString("fileExt");

    public final StringPath fileFieldName = createString("fileFieldName");

    public final StringPath fileName = createString("fileName");

    public final StringPath filePath = createString("filePath");

    public final NumberPath<Long> fileSize = createNumber("fileSize", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> regDt = _super.regDt;

    //inherited
    public final StringPath regId = _super.regId;

    public QFileBaseEntity(String variable) {
        super(FileBaseEntity.class, forVariable(variable));
    }

    public QFileBaseEntity(Path<? extends FileBaseEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QFileBaseEntity(PathMetadata metadata) {
        super(FileBaseEntity.class, metadata);
    }

}

