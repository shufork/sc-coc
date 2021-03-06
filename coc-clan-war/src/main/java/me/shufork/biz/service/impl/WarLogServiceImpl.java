package me.shufork.biz.service.impl;

import lombok.extern.slf4j.Slf4j;
import me.shufork.biz.domain.CocWarLog;
import me.shufork.biz.repository.CocWarLogRepository;
import me.shufork.biz.service.WarLogService;
import me.shufork.biz.service.WarTeamService;
import me.shufork.biz.utils.EntityKeyUtils;
import me.shufork.biz.vo.WarLogEntryClanVo;
import me.shufork.biz.vo.WarLogEntryVo;
import me.shufork.common.dto.supercell.coc.WarLogEntryDto;
import me.shufork.common.utils.CocDateTimeUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;

@Transactional
@Service
@Slf4j
public class WarLogServiceImpl implements WarLogService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private WarTeamService warTeamService;

    @Autowired
    private CocWarLogRepository cocWarLogRepository;

    private String addWarLogIfMiss(WarLogEntryDto warLog){
        final WarLogEntryClanVo clan = modelMapper.map(warLog.getClan(),WarLogEntryClanVo.class);
        clan.setWarTime(CocDateTimeUtil.parse(warLog.getEndTime()).toDate());
        clan.setOpponent(warLog.getOpponent().getTag());

        final WarLogEntryClanVo opponent = modelMapper.map(warLog.getOpponent(),WarLogEntryClanVo.class);
        opponent.setWarTime(CocDateTimeUtil.parse(warLog.getEndTime()).toDate());
        opponent.setOpponent(warLog.getClan().getTag());

        final String homeTeamId = warTeamService.insertOrUpdate(clan);
        final String awayTeamId = warTeamService.insertOrUpdate(opponent);

        final String pk = EntityKeyUtils.keyOfWarLog(clan.getTag(),warLog.getEndTime());

        if(cocWarLogRepository.exists(pk)){
            return pk;
        }
        if(homeTeamId == null || awayTeamId == null){
            throw new IllegalStateException("Failed to create war team");
        }
        CocWarLog entity = new CocWarLog();
        entity.setId(pk);
        entity.setHomeTeam(homeTeamId);
        entity.setAwayTeam(awayTeamId);
        entity.setResult(warLog.getResult());
        entity.setEndTime(CocDateTimeUtil.parse(warLog.getEndTime()).toDate() );
        entity.setTeamSize(warLog.getTeamSize());
        entity.setOwner(clan.getTag());

        cocWarLogRepository.insertOrUpdate(entity);
        return pk;
    }

    private List<String> addWarLogIfMiss(Iterable<WarLogEntryDto> warLogs){
        List<String> cocWarLogs = new LinkedList<>();
        warLogs.forEach(o->{
            cocWarLogs.add(addWarLogIfMiss(o));
        });
        return cocWarLogs;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    @Override
    public void updateWarLog(Iterable<WarLogEntryDto> warLogs) {
        addWarLogIfMiss(warLogs);
    }

    @Override
    public Page<WarLogEntryVo> getWarLog(String clanTag, Pageable pageable) {
        return cocWarLogRepository.findAllByHomeTeam(clanTag,pageable).map(o->modelMapper.map(o, WarLogEntryVo.class));
    }
}
