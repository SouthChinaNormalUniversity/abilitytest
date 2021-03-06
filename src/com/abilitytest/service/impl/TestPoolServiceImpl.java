package com.abilitytest.service.impl;

import java.util.Date;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.abilitytest.common.Page;
import com.abilitytest.dao.TestPoolDao;
import com.abilitytest.entity.TestPool;
import com.abilitytest.service.TestPoolService;

@Service("testPoolService")
public class TestPoolServiceImpl extends BaseServiceImpl<TestPool> implements TestPoolService {

	@Autowired
	private TestPoolDao testPoolDao;
	
	@Override
	public Page getTestList(Map<String, Object> map){
		String sql = " from testpool as b JOIN disabledman as a on a.id=b.person_id"
				+ " where a.if_del=0 and b.if_del=0";
		StringBuilder builder = new StringBuilder(sql);
		Object id_number = map.get("id_number");
		if(id_number!=null&&!((String)id_number).trim().equals(""))
			builder.append(" and a.id_number='"+((String)id_number)+"'");
		Object name = map.get("name");
		if(name!=null&&!((String)name).trim().equals(""))
			builder.append(" and a.`name`='"+((String)name)+"'");
		Object startTime = map.get("startTime");
		if(startTime!=null&&!((String)startTime).trim().equals(""))
			builder.append(" and b.modifytime>='"+(String)startTime+"'");
		Object endTime = map.get("endTime");
		if(endTime!=null&&!((String)endTime).trim().equals(""))
			builder.append(" and b.modifytime<='"+((String)endTime)+" 23:59:59'");
		Object pageNum = map.get("pageNum");
		int pagenum = 1;
		int pagesize = 10;
		if(pageNum!=null&&!((String)pageNum).trim().equals(""))
			if(((String)pageNum).trim().equals("*"))
				return testPoolDao.getTestList(builder.toString(), pagenum,pagesize,false);
			else if(Integer.parseInt((String)pageNum)>1)
				pagenum = Integer.parseInt((String)pageNum);
		Object pageSize = map.get("pageSize");
		if(pageSize!=null&&!((String)pageSize).trim().equals("")&&Integer.parseInt((String)pageSize)>0)
			pagesize = Integer.parseInt((String)pageSize);
		return testPoolDao.getTestList(builder.toString(), pagenum,pagesize,true);
	}

	@Override
	public Integer addOrUpdateTest(Map<String, Object> map) {
		Integer id = null;
		try {
			if(map.containsKey("testpool_id")&&!((String)map.get("testpool_id")).equals("")){
				TestPool pool = testPoolDao.getByIdWithoutDel(Integer.valueOf((String)map.get("testpool_id")));
				if(pool != null){
					pool.setTest((String)map.get("test"));
					pool.setModifytime(new Date());
					testPoolDao.update(pool);
					return pool.getId();
				}				
			}
			Date date = new Date();
			TestPool testPool = new TestPool(Integer.valueOf((String)map.get("person_id"))
					, (String)map.get("test"), 0, date, date);
			testPoolDao.add(testPool);
			id = testPool.getId();
		} catch (Exception e) {
			id = null;
		}		
		return id;
	}
}
