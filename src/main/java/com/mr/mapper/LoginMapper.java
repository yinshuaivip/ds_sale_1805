package com.mr.mapper;

import com.mr.model.TMallUserAccount;
import org.apache.ibatis.annotations.Param;

/**
 * Created by YinShuai on 2018/11/5.
 */
public interface LoginMapper {

    TMallUserAccount login(@Param("yhMch") String yhMch,@Param("yhMm") String yhMm);
}
