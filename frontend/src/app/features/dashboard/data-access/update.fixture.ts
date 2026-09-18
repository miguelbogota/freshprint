import type { EngagementUpdate } from './update.model';

/** API-shaped data for isolated UI tests. No engagement file is loaded. */
export const updateFixture: EngagementUpdate[] = [
  {
    engagementId: 'ENG-1007',
    name: 'Bluewater Hospitality 2026',
    template: { id: 'REVIEW-CA', displayName: 'Canadian Review Engagement' },
    status: 'PENDING',
    baselineVersion: 6,
    targetVersion: 8,
    pendingVersionCount: 2,
    summary: {
      state: 'AVAILABLE',
      generatedAt: '2026-08-25T13:04:55Z',
      groups: [
        {
          section: 'Inquiries',
          changes: [
            {
              kind: 'ADDED',
              description:
                'Added question: "Describe any events after the reporting date that may require adjustment or disclosure."',
              reviewRecommended: false,
            },
          ],
        },
        {
          section: 'Analytics',
          changes: [
            {
              kind: 'CHANGED',
              description: 'Tolerance changed from 0.15 to 0.1.',
              reviewRecommended: false,
            },
          ],
        },
      ],
    },
    freshness: { state: 'FRESH', checkedAt: '2026-08-25T13:05:00Z' },
  },
  {
    engagementId: 'ENG-1003',
    name: 'Harbourview Logistics 2026',
    template: { id: 'AUDIT-CA', displayName: 'Canadian Audit Engagement' },
    status: 'PENDING',
    baselineVersion: 3,
    targetVersion: 5,
    pendingVersionCount: 2,
    summary: { state: 'COMPUTING', reason: 'SUMMARY_IN_PROGRESS' },
    freshness: { state: 'FRESH', checkedAt: '2026-08-25T13:05:00Z' },
  },
  {
    engagementId: 'ENG-1005',
    name: 'Cedar Peak Services 2026',
    template: { id: 'REVIEW-CA', displayName: 'Canadian Review Engagement' },
    status: 'CURRENT',
    baselineVersion: 8,
    targetVersion: 8,
    pendingVersionCount: 0,
    freshness: { state: 'FRESH', checkedAt: '2026-08-25T13:05:00Z' },
  },
  {
    engagementId: 'ENG-UNKNOWN',
    name: 'New engagement awaiting metadata',
    template: { id: 'AUDIT-CA', displayName: 'Canadian Audit Engagement' },
    status: 'UNKNOWN',
    statusReason: 'ENGAGEMENT_METADATA_UNAVAILABLE',
    baselineVersion: 0,
    targetVersion: 0,
    pendingVersionCount: 0,
    freshness: { state: 'STALE', checkedAt: '2026-08-24T13:05:00Z' },
  },
];
