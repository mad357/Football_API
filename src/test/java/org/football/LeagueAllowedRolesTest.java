package org.football;

import org.football.country.CountryDto;
import org.football.league.League;
import org.football.league.LeagueDto;
import org.football.league.LeagueRepository;
import org.football.league.LeagueService;
import org.football.util.validationgroups.Create;
import org.football.util.validationgroups.Update;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import org.football.country.Country;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.*;
import org.mockito.stubbing.Answer;

import javax.validation.ConstraintViolation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LeagueAllowedRolesTest {

    @Mock
    LeagueRepository leagueRepository;

    @InjectMocks
    LeagueService leagueService;

    static List<League> mockList;

    @BeforeAll
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockList = new ArrayList<>();
        Country c = new Country();
        c.setId(1L);
        League l = new League();
        l.setId(1L);
        l.setCountry(c);
        l.setName("PKO Ekstraklasa");
        mockList.add(l);
        l = new League();
        l.setId(2L);
        l.setCountry(c);
        l.setName("1 liga");
        mockList.add(l);
        l = new League();
        l.setId(3L);
        l.setCountry(c);
        l.setName("2 liga");
        mockList.add(l);

        doAnswer((Answer<Object>) invocation -> {
            League l1 = (League) invocation.getArguments()[0];
            l1.setId(-1L);
            return null;
        }).when(leagueRepository).persist(any(League.class));

        final PanacheQuery query = Mockito.mock(PanacheQuery.class);
        final Object[][] args = new Object[1][1];
        when(leagueRepository.find(any(String.class), ArgumentMatchers.<Object>any())).thenAnswer(
            (Answer) invocation -> {
               args[0] = invocation.getArguments();
                return query;
            }
        );
        when(leagueRepository.find("name = ?1 and country = ?2", (Object) eq(any())).firstResult())
            .thenAnswer(
            (Answer) invocation -> {
                List<League> results = mockList.stream()
                        .filter(x -> x.getName().equals(args[0][1]) && x.getCountry().getId().equals(((Country)args[0][2]).getId()))
                        .collect(Collectors.toList());
                return results.size() > 0 ? results.get(0) : null;
            }
        );
    }

    @Test()
    public void checkFieldsDuringActions(){
        CountryDto c1 = new CountryDto();
        c1.setId(1L);
        LeagueDto l = new LeagueDto();
        Set<ConstraintViolation<LeagueDto>> errors = AppConfig.getValidator().validate(l, Create.class);
        assertEquals(3, errors.size());
        errors = AppConfig.getValidator().validate(l, Update.class);
        assertEquals(4, errors.size());
        l.setId(1L);
        errors = AppConfig.getValidator().validate(l, Update.class);
        assertEquals(3, errors.size());
        l.setCountryId(c1.getId());
        errors = AppConfig.getValidator().validate(l, Update.class);
        assertEquals(2, errors.size());
        l.setName("2 liga");
        errors = AppConfig.getValidator().validate(l, Update.class);
        assertEquals(1, errors.size());
        l.setClubNumber((short) 20);
        errors = AppConfig.getValidator().validate(l, Update.class);
        assertEquals(0, errors.size());
    }

    @Test()
    public void createLeague() {
        Country c1 = new Country();
        c1.setId(1L);
        Country c2 = new Country();
        c2.setId(2L);
        League l = new League();
        l.setCountry(c1);
        l.setName("2 liga");
        l.setClubNumber((short) 20);
        Exception exception = assertThrows(RuntimeException.class, () -> leagueService.create(l));
        assertTrue(exception.getMessage().contains("League already exist"));
        l.setCountry(c2);
        long result =  leagueService.create(l);
        assertEquals(-1L, result);
        l.setId(null);
        l.setCountry(c1);
        l.setName("3 liga");
        result = leagueService.create(l);
        assertEquals(-1L, result);
    }
}
