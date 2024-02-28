package com.user.usercenter.vo;


import com.user.usercenter.model.Team;
import lombok.Data;

import java.io.Serializable;

/**
 *
 */
@Data
public class PageTeam extends Team implements Serializable{

    private static final long serialVersionUID = 1L;

    private int PageSize = 10;

    private int PageNum = 1;


}
