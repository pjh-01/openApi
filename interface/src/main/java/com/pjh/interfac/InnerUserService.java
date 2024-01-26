package com.pjh.interfac;

import com.pjh.interfac.model.User;

public interface InnerUserService {
    User getInvokerUser(String accessKey);
}
