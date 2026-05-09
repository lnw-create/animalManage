package com.hutb.ai.mapper;

import com.hutb.ai.model.ChatSession;
import com.hutb.ai.model.ChatSessionVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ChatSessionMapper {

    @Insert("INSERT INTO chat_session (session_id, user_id, title, status, create_time, update_time) " +
            "VALUES (#{sessionId}, #{userId}, #{title}, '1', now(), now())")
    int insert(ChatSession session);

    @Select("SELECT * FROM chat_session WHERE session_id = #{sessionId} AND status = '1'")
    ChatSession findBySessionId(@Param("sessionId") String sessionId);

    @Select("SELECT session_id, title, create_time, update_time FROM chat_session " +
            "WHERE user_id = #{userId} AND status = '1' " +
            "ORDER BY update_time DESC")
    List<ChatSessionVO> listByUser(@Param("userId") Long userId);

    @Update("UPDATE chat_session SET title = #{title}, update_time = now() " +
            "WHERE session_id = #{sessionId} AND user_id = #{userId} AND status = '1'")
    int updateTitle(@Param("sessionId") String sessionId,
                    @Param("userId") Long userId,
                    @Param("title") String title);

    @Update("UPDATE chat_session SET update_time = now() " +
            "WHERE session_id = #{sessionId} AND status = '1'")
    int touch(@Param("sessionId") String sessionId);

    @Update("UPDATE chat_session SET status = '-1', update_time = now() " +
            "WHERE session_id = #{sessionId} AND user_id = #{userId} AND status = '1'")
    int softDelete(@Param("sessionId") String sessionId,
                   @Param("userId") Long userId);
}
