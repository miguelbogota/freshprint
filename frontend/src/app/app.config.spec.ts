import { HttpClient } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { appConfig } from './app.config';

describe('appConfig', () => {
  it('provides the HTTP client and router used by the dashboard', () => {
    TestBed.configureTestingModule({ providers: appConfig.providers });

    expect(TestBed.inject(HttpClient)).toBeTruthy();
    expect(TestBed.inject(Router).config[0].path).toBe('');
  });
});
