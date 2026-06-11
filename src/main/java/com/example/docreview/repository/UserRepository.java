package com.example.docreview.repository;

import com.example.docreview.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
//這整個介面只有一行，但繼承了 JpaRepository 之後，自動擁有以下所有方法，不用寫任何程式碼：
//findAll() 查詢所有資料
//findById(id) 依照id查詢單筆資料
//save(entity) 新增或更新
//deleteById(id) 依照id刪除
//count()  計算筆數
//existsById(id)  確認是否存在
//<User, Long> 的意思是：「這個 Repository 管理 User 這張表，主鍵型別是 Long」。