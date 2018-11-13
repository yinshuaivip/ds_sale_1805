package com.mr.serviceImpl;

import com.mr.mapper.OrderMapper;
import com.mr.model.TMallAddress;
import com.mr.model.TMallFlowVO;
import com.mr.model.TMallOrderInfo;
import com.mr.model.TMallOrderVO;
import com.mr.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by YinShuai on 2018/11/12.
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;


    public List<TMallAddress> listAddressByYhId(Integer id) {
        return orderMapper.listAddressByYhId(id);
    }

    public void saveOrder(TMallOrderVO orderVO) {
        List<Integer> cartIds = new ArrayList<Integer>();
        //增加 order
        orderMapper.saveOrder(orderVO);

        //获取到orderId ，然后增加 List<flow>、for循环增加
        List<TMallFlowVO> flowList = orderVO.getFlowList();
        for (int i = 0; i < flowList.size(); i++) {
            Map flowMap = new HashMap();
            TMallFlowVO flowvo = flowList.get(i);
            flowMap.put("flow",flowvo);
            flowMap.put("orderId",orderVO.getId());
            orderMapper.saveFlow(flowMap);

            //增加orderInfo 、批量增加
            List<TMallOrderInfo> infoList = flowList.get(i).getInfoList();
            Map infoMap = new HashMap();
            infoMap.put("infoList",infoList);
            infoMap.put("flowId",flowvo.getId());
            infoMap.put("orderId",orderVO.getId());

            orderMapper.saveInfo(infoMap);

            for (int j = 0; j < infoList.size(); j++) {
                TMallOrderInfo info = infoList.get(j);
                cartIds.add(info.getGwchId());
            }
        }

        //删除购物车
        orderMapper.deleteCartsByCartIds(cartIds);
    }
}
