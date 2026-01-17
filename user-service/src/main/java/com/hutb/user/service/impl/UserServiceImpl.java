package com.hutb.user.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hutb.commonUtils.exception.CommonException;
import com.hutb.commonUtils.utils.UserContext;
import com.hutb.user.constant.UserCommonConstant;
import com.hutb.user.mapper.userMapper;
import com.hutb.user.model.DTO.PageQueryListDTO;
import com.hutb.user.model.DTO.UserDTO;
import com.hutb.user.model.VO.LoginResponse;
import com.hutb.user.model.pojo.PageInfo;
import com.hutb.user.model.pojo.User;
import com.hutb.user.service.UserService;
import com.hutb.user.utils.CommonValidate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private userMapper userMapper;

    /**
     * 添加用户
     * @param userDTO 用户信息
     */
    @Override
    public void addUser(UserDTO userDTO) throws CommonException {
        log.info("添加用户:{}",userDTO);
        //1. 参数校验
        CommonValidate.userValidate(userDTO);

        //2. 判断用户是否存在
        queryUserByUsernameAndPhone(userDTO);

        //4.新增
        userDTO.setStatus(UserCommonConstant.USER_STATUS_ENABLE);
        userDTO.setRole(UserCommonConstant.USER_ROLE_NORMAL);
        userDTO.setCreateUser(UserContext.getUsername());
        userDTO.setUpdateUser(UserContext.getUsername());
        userDTO.setCreateTime(new Date());
        userDTO.setUpdateTime(new Date());
        userMapper.addUser(userDTO);
        log.info("添加用户成功");
    }

    /**
     * 删除用户
     * @param id 用户id
     */
    @Override
    public void removeUser(Long id) throws CommonException {
        log.info("删除用户:id-{}",id);
        //1.参数校验
        if (id == null || id <= 0){
            throw new CommonException("删除用户id不能为空");
        }
        //2.判断用户是否存在
        User user = userMapper.queryUserById(id);
        if (user == null){
            throw new CommonException("用户不存在");
        }
        //4.删除
        long remove = userMapper.removeUser(id, UserContext.getUsername(), UserCommonConstant.USER_STATUS_DELETE);
        if (remove == 0){
            throw new CommonException("删除用户失败");
        }
        log.info("删除用户成功");
    }

    /**
     * 更新用户
     * @param userDTO 用户信息
     */
    @Override
    public void updateUser(UserDTO userDTO) throws CommonException {
        log.info("更新用户信息:{}",userDTO);
        //1.参数校验
        Long id = userDTO.getId();
        if (id == null || id <= 0){
            throw new CommonException("更新用户id不能为空");
        }
        CommonValidate.userValidate(userDTO);

        //2.查询用户信息
            User user = userMapper.queryUserById(id);
        if (user == null){
            throw new CommonException("用户信息不存在");
        }

        //3.查询更新的用户信息是否存在
        queryUserByUsernameAndPhone(userDTO);

        //4.更新用户
        userDTO.setUpdateUser(UserContext.getUsername());
        userDTO.setUpdateTime(new Date());
        long update = userMapper.updateUser(userDTO);
        if (update == 0){
            throw new CommonException("更新用户信息失败");
        }
        log.info("更新用户信息成功");
    }

    /**
     * 查询用户列表
     * @param pageQueryListDTO 分页查询参数
     * @return 用户列表
     */
    @Override
    public PageInfo queryUserList(PageQueryListDTO pageQueryListDTO) {
        log.info("查询用户列表:{}",pageQueryListDTO);
        //分页查询
        Page<Object> page = PageHelper.startPage(pageQueryListDTO.getPageNum(), pageQueryListDTO.getPageSize());
        List<User> users = userMapper.queryUserList(pageQueryListDTO);
        com.github.pagehelper.PageInfo<User> pageInfo = new com.github.pagehelper.PageInfo<>(users);
        log.info("查询用户列表成功");
        return new PageInfo(pageInfo.getTotal(), pageInfo.getList());
    }

    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return 登录响应
     */
    @Override
    public LoginResponse login(String username, String password) {
        log.info("用户登录: username={}", username);
        
        // 1. 参数校验
        if (username == null || username.trim().isEmpty()) {
            throw new CommonException("用户名不能为空");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new CommonException("密码不能为空");
        }
        
        // 2. 查询用户信息
        User user = userMapper.queryUserByUsername(username);
        if (user == null) {
            throw new CommonException("用户名或密码错误");
        }
        
        // 3. 验证密码
        if (!password.equals(user.getPassword())) {
            throw new CommonException("用户名或密码错误");
        }
        
        // 4. 检查用户状态
        if (!UserCommonConstant.USER_STATUS_ENABLE.equals(user.getStatus())) {
            throw new CommonException("账号已被禁用");
        }
        
        // 5. 生成JWT token
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("username", user.getUsername());
        
        // 使用与网关一致的固定密钥和过期时间
        long timeout = 24 * 60 * 60 * 1000; // 24小时
                        
        String token = com.hutb.commonUtils.utils.JwtUtil.createJwt("MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC8FgCz6/n59Z6VX5xtzvQ4aCU2oIqxERUd/Qk5uVQ2WMZS6OfmvmP3ZQ+Oo+2y1E+W8yaZTSVXVI2ztNxJJNkMSQX+uCv3+6FbX6W//R/1DhXD7XkXiPx2+6NgljEiKCw+7g1y4UlywX1m0JDlPSqphGyWTybD4m37Xy/cJwIDAQAB", timeout, claims);
        
        log.info("用户登录成功: id={}", user.getId());
        
        // 返回登录响应对象
        return new LoginResponse(user.getId(), user.getUsername(), "user", token);
    }

    /**
     * 用户注册
     * @param userDTO 用户信息
     */
    @Override
    public void registerUser(UserDTO userDTO) throws CommonException {
        log.info("用户注册: {}", userDTO);
        
        // 1. 参数校验
        CommonValidate.userValidate(userDTO);
        
        // 2. 检查用户名和手机号是否已存在
        checkUserByUsernameAndPhone(userDTO);
        
        // 3. 设置注册用户的默认值
        userDTO.setStatus(UserCommonConstant.USER_STATUS_ENABLE); // 默认启用状态
        userDTO.setRole(UserCommonConstant.USER_ROLE_NORMAL);
        userDTO.setCreateUser(userDTO.getUsername()); // 注册用户设置为用户名
        userDTO.setUpdateUser(userDTO.getUsername()); // 注册用户设置为用户名
        userDTO.setCreateTime(new Date());
        userDTO.setUpdateTime(new Date());
        
        // 4. 执行注册
        userMapper.addUser(userDTO);
        log.info("用户注册成功");
    }

    /**
     * 根据手机号和姓名查询用户信息
     * @param user 用户信息
     */
    private void queryUserByUsernameAndPhone(UserDTO user){
        User userAnother = userMapper.queryUserByUsername(user.getUsername());
        User userAnotherAnother = userMapper.queryUserByPhone(user.getPhone());
        if (userAnother != null && !userAnother.getId().equals(user.getId())){
            throw new CommonException("用户名已存在");
        }
        if (userAnotherAnother != null && !userAnotherAnother.getId().equals(user.getId())){
            throw new CommonException("手机号已存在");
        }
    }
    
    /**
     * 检查用户名和手机号是否已存在（用于注册）
     * @param user 用户信息
     */
    private void checkUserByUsernameAndPhone(UserDTO user){
        User userByUsername = userMapper.queryUserByUsername(user.getUsername());
        User userByPhone = userMapper.queryUserByPhone(user.getPhone());
        
        if (userByUsername != null) {
            throw new CommonException("用户名已存在");
        }
        
        if (userByPhone != null) {
            throw new CommonException("手机号已存在");
        }
    }

    //todo:更新用户权限接口，用户注册志愿者时调用修改角色
}