package com.vartool.web.model.entity.user;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QUserPreferencesEntity is a Querydsl query type for UserPreferencesEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserPreferencesEntity extends EntityPathBase<UserPreferencesEntity> {

    private static final long serialVersionUID = 342330141L;

    public static final QUserPreferencesEntity userPreferencesEntity = new QUserPreferencesEntity("userPreferencesEntity");

    public final com.vartool.web.model.entity.base.QAabstractAuditorModel _super = new com.vartool.web.model.entity.base.QAabstractAuditorModel(this);

    public final StringPath groupId = createString("groupId");

    public final StringPath prefKey = createString("prefKey");

    public final StringPath prefVal = createString("prefVal");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> regDt = _super.regDt;

    //inherited
    public final StringPath regId = _super.regId;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updDt = _super.updDt;

    //inherited
    public final StringPath updId = _super.updId;

    public final StringPath viewid = createString("viewid");

    public QUserPreferencesEntity(String variable) {
        super(UserPreferencesEntity.class, forVariable(variable));
    }

    public QUserPreferencesEntity(Path<? extends UserPreferencesEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUserPreferencesEntity(PathMetadata metadata) {
        super(UserPreferencesEntity.class, metadata);
    }

}

