package ru.otus.bank.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import ru.otus.bank.dao.AgreementDao;
import ru.otus.bank.entity.Agreement;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AgreementServiceImplTest {

    private AgreementDao dao = Mockito.mock(AgreementDao.class);

    AgreementServiceImpl agreementServiceImpl;

    @BeforeEach
    void init() {
        agreementServiceImpl = new AgreementServiceImpl(dao);
    }

    @Test
    public void testFindByName() {
        String name = "test";
        Agreement agreement = new Agreement();
        agreement.setId(10L);
        agreement.setName(name);

        when(dao.findByName(name)).thenReturn(
                Optional.of(agreement));

        Optional<Agreement> result = agreementServiceImpl.findByName(name);

        Assertions.assertTrue(result.isPresent());
        assertEquals(10, agreement.getId());
    }

    @Test
    public void testFindByNameWithCaptor() {
        String name = "test";
        Agreement agreement = new Agreement();
        agreement.setId(10L);
        agreement.setName(name);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

        when(dao.findByName(captor.capture())).thenReturn(
                Optional.of(agreement));

        Optional<Agreement> result = agreementServiceImpl.findByName(name);

        assertEquals("test", captor.getValue());
        Assertions.assertTrue(result.isPresent());
        assertEquals(10, agreement.getId());
    }

    @Test
    public void testFindByNameWithCaptorVerification() {
        String name = "test";
        Agreement agreement = new Agreement();
        agreement.setId(10L);
        agreement.setName(name);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

        when(dao.findByName(captor.capture())).thenReturn(Optional.of(agreement));

        agreementServiceImpl.findByName(name);

        verify(dao).findByName(captor.capture());
        assertEquals("test", captor.getValue());
    }

    @Test
    public void testAddAgreementThrowsException() {
        String name = "errorCase";
        when(dao.save(any())).thenThrow(new RuntimeException("Database error"));

        Assertions.assertThrows(RuntimeException.class, () -> agreementServiceImpl.addAgreement(name));
    }

    @Test
    public void testAddAgreement() {
        String name = "newAgreement";
        Agreement savedAgreement = new Agreement();
        savedAgreement.setId(1L);
        savedAgreement.setName(name);

        when(dao.save(any())).thenReturn(savedAgreement);

        Agreement result = agreementServiceImpl.addAgreement(name);

        assertEquals(name, result.getName());
        assertEquals(1L, result.getId());
        verify(dao).save(any(Agreement.class));  // вызван ли save
    }



}
