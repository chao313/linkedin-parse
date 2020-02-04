<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd" >
<mapper namespace="${allFtl.daoFtl.packageName}.${allFtl.daoFtl.className}">

    <resultMap id="resultMap" type="${allFtl.voFtl.packageName}.${allFtl.voFtl.className}">
      <#list ftlVo.mysqlAndJavaFields as mysqlAndJavaField>
        <result column="${mysqlAndJavaField.mysqlField.name}" property="${mysqlAndJavaField.javaField.name}"/>
      </#list>
    </resultMap>

    <insert id="insert"
            parameterType="${allFtl.voFtl.packageName}.${allFtl.voFtl.className}">
        INSERT INTO `${ftlVo.table.tableName}`(
      <#list ftlVo.mysqlFields as field>
        `${field.name}` <#if field_has_next>,</#if>
      </#list>
        )VALUE(
      <#list ftlVo.javaFields as field>
          <#noparse>#{</#noparse>${field.name}<#noparse>}</#noparse>  <#if field_has_next>,</#if>
      </#list>
        )
    </insert>

    <insert id="inserts"
            parameterType="java.util.List">
        INSERT INTO `${ftlVo.table.tableName}`(
        <#list ftlVo.mysqlFields as field>
        `${field.name}`<#if field_has_next>,</#if>
        </#list>
        )VALUES
        <foreach collection="vos" item="item" separator=",">
            (
         <#list ftlVo.javaFields as field>
            <#noparse>#{item.</#noparse>${field.name}<#noparse>}</#noparse><#if field_has_next>,</#if>
         </#list>
            )
        </foreach>
    </insert>

    <select id="queryBase" resultMap="resultMap"
            resultType="${allFtl.voFtl.packageName}.${allFtl.voFtl.className}"
            parameterType="${allFtl.voFtl.packageName}.${allFtl.voFtl.className}">
        SELECT
      <#list ftlVo.mysqlFields as field>
        `${field.name}`<#if field_has_next>,</#if>
      </#list>
        FROM `${ftlVo.table.tableName}`
        <where>
            1 = 1
        <#list ftlVo.mysqlAndJavaFields as mysqlAndJavaField>
            <if test="${mysqlAndJavaField.javaField.name} != null">
                AND `${mysqlAndJavaField.mysqlField.name}` = <#noparse>#{</#noparse>${mysqlAndJavaField.javaField.name}<#noparse>}</#noparse>
            </if>
        </#list>
        </where>
    </select>

    <update id="updateBase"
            parameterType="${allFtl.voFtl.packageName}.${allFtl.voFtl.className}">
        UPDATE `${ftlVo.table.tableName}`
        <set>
        <#list ftlVo.mysqlAndJavaFields as mysqlAndJavaField>
            <#if mysqlAndJavaField.mysqlField.isPRI=false>
            <if test="source.${mysqlAndJavaField.javaField.name} != null">
                `${mysqlAndJavaField.mysqlField.name}` = <#noparse>#{source.</#noparse>${mysqlAndJavaField.javaField.name}<#noparse>}</#noparse><#if mysqlAndJavaField_has_next>,</#if>
            </if>
            </#if>
        </#list>
        </set>
        <where>
            1 =1
        <#list ftlVo.mysqlAndJavaFields as mysqlAndJavaField>
            <if test="target.${mysqlAndJavaField.javaField.name} != null">
                AND `${mysqlAndJavaField.mysqlField.name}` = <#noparse>#{target.</#noparse>${mysqlAndJavaField.javaField.name}<#noparse>}</#noparse>
            </if>
        </#list>
        </where>
    </update>

    <update id="updateBaseIncludeNull"
            parameterType="${allFtl.voFtl.packageName}.${allFtl.voFtl.className}">
        UPDATE `${ftlVo.table.tableName}`
        <set>
         <#list ftlVo.mysqlAndJavaFields as mysqlAndJavaField>
             <#if mysqlAndJavaField.mysqlField.isPRI=false>
            `${mysqlAndJavaField.mysqlField.name}` = <#noparse>#{source.</#noparse>${mysqlAndJavaField.javaField.name}<#noparse>}</#noparse><#if mysqlAndJavaField_has_next>,</#if>
             </#if>
         </#list>
        </set>
        <where>
            1 =1
         <#list ftlVo.mysqlAndJavaFields as mysqlAndJavaField>
            <if test="target.${mysqlAndJavaField.javaField.name} != null">
                AND `${mysqlAndJavaField.mysqlField.name}` = <#noparse>#{target.</#noparse>${mysqlAndJavaField.javaField.name}<#noparse>}</#noparse>
            </if>
         </#list>
        </where>
    </update>

    <delete id="deleteBase"
            parameterType="${allFtl.voFtl.packageName}.${allFtl.voFtl.className}">
        DELETE FROM `${ftlVo.table.tableName}`
        <where>
            1 =1
        <#list ftlVo.mysqlAndJavaFields as mysqlAndJavaField>
            <if test="${mysqlAndJavaField.javaField.name} != null">
                AND `${mysqlAndJavaField.mysqlField.name}` = <#noparse>#{</#noparse>${mysqlAndJavaField.javaField.name}<#noparse>}</#noparse>
            </if>
        </#list>
        </where>
    </delete>

<#if ftlVo.primaryKeyJavaFields?? && (ftlVo.primaryKeyJavaFields?size>0) >

    <select id="queryByPrimaryKey" resultMap="resultMap"
            resultType="${allFtl.voFtl.packageName}.${allFtl.voFtl.className}">
        SELECT
         <#list ftlVo.mysqlFields as field>
        `${field.name}`<#if field_has_next>,</#if>
         </#list>
        FROM `${ftlVo.table.tableName}`
        <where>
            1 =1
         <#list ftlVo.primaryKeyMysqlAndJavaFields as mysqlAndJavaField>
            AND `${mysqlAndJavaField.mysqlField.name}` = <#noparse>#{</#noparse>${mysqlAndJavaField.javaField.name}<#noparse>}</#noparse>
         </#list>
        </where>
    </select>

    <delete id="deleteByPrimaryKey">
        DELETE FROM `${ftlVo.table.tableName}`
        <where>
            1 = 1
            <#list ftlVo.primaryKeyMysqlAndJavaFields as mysqlAndJavaField>
               AND `${mysqlAndJavaField.mysqlField.name}` = <#noparse>#{</#noparse>${mysqlAndJavaField.javaField.name}<#noparse>}</#noparse>
            </#list>
        </where>
    </delete>
</#if>

</mapper>