package com.handicraft.core.service.Events;

import com.handicraft.core.dto.Events.EventToUser;
import com.handicraft.core.repository.Events.EventToUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class EventToUserService {

    @Autowired
    EventToUserRepository eventToUserRepository;

    public EventToUser insert(EventToUser eventToUser)
    {
        return eventToUserRepository.save(eventToUser);
    }

    public EventToUser update(EventToUser eventToUser)
    {
        return eventToUserRepository.save(eventToUser);
    }

    public List<EventToUser> find()
    {
        return eventToUserRepository.findAll();
    }

    public List<EventToUser> findByDate(Date start , Date end)
    {
        return eventToUserRepository.findByStartGreaterThanEqualAndEndLessThanEqual(start , end);
    }

    @Transactional
    public void remove(long eid)
    {
        eventToUserRepository.delete(eid);
    }

}
