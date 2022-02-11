package com.vartool.web.model.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QFileInfoEntity is a Querydsl query type for FileInfoEntity
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QFileInfoEntity extends EntityPathBase<FileInfoEntity> {

    private static final long serialVersionUID = 867687837L;

    public static final QFileInfoEntity fileInfoEntity = new QFileInfoEntity("fileInfoEntity");

    public final com.vartool.web.model.entity.base.QAbstractRegAuditorModel _super = new com.vartool.web.model.entity.base.QAbstractRegAuditorModel(this);

    public final StringPath contGroupId = createString("contGroupId");

    public final StringPath fileContId = createString("fileContId");

    public final StringPath fileDiv = createString("fileDiv");

    public final StringPath fileExt = createString("fileExt");

    public final StringPath fileFieldName = createString("fileFieldName");

    public final StringPath fileId = createString("fileId");

    public final StringPath fileName = createString("fileName");

    public final StringPath filePath = createString("filePath");

    public final NumberPath<Long> fileSize = createNumber("fileSize", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> regDt = _super.regDt;

    //inherited
    public final StringPath regId = _super.regId;

    public QFileInfoEntity(String variable) {
        super(FileInfoEntity.class, forVariable(variable));
    }

    public QFileInfoEntity(Path<? extends FileInfoEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QFileInfoEntity(PathMetadata metadata) {
        super(FileInfoEntity.class, metadata);
    }

}

