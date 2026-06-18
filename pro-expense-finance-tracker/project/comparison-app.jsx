// Platform comparison — Flow 04 (First Launch) + Profile Setup, iOS vs Android.
// Each artboard pairs the existing iOS screen (left) with its Material 3
// Android counterpart (right) so the two platforms read side-by-side.

const PC_GAP = 40;
const PAIR_W = 390;                        // single phone width
const PAIR_H = (844 + 40) * 2 + PC_GAP;    // two stacked (phone + caption) + gap
const PAIR_ABS = { background: 'transparent', borderRadius: 24, overflow: 'visible', boxShadow: 'none', padding: 0 };

function PlatformCaption({ children }) {
  return (
    <div style={{ display: 'flex', alignItems: 'center', gap: 8, height: 28, marginBottom: 12, paddingLeft: 4 }}>
      <span style={{ fontFamily: 'var(--mono)', fontSize: 11, fontWeight: 600, letterSpacing: '0.1em', textTransform: 'uppercase', color: 'rgba(60,50,40,0.7)' }}>{children}</span>
    </div>
  );
}

// Stacks an iOS frame (top) over its Android counterpart (bottom) in one artboard.
function Pair({ ios, android }) {
  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: PC_GAP, alignItems: 'flex-start' }}>
      <div>
        <PlatformCaption>iOS · Human Interface</PlatformCaption>
        {ios}
      </div>
      <div>
        <PlatformCaption>Android · Material 3</PlatformCaption>
        {android}
      </div>
    </div>
  );
}

function ComparisonApp() {
  return (
    <DesignCanvas accent="#039be5" background="#1f1a14">

      {/* ─── FLOW 04 — First Launch ─── */}
      <DCSection id="first-launch" title="Flow 04 — First Launch · iOS vs Android" subtitle="Splash → onboarding · iOS Human Interface (top) paired with Material 3 (bottom)">
        <DCArtboard id="splash" label="01 · Splash" width={PAIR_W} height={PAIR_H} style={PAIR_ABS}>
          <Pair ios={<ScreenSplashHi />} android={<AndSplash />} />
        </DCArtboard>
        <DCArtboard id="onb-1" label="02 · Welcome" width={PAIR_W} height={PAIR_H} style={PAIR_ABS}>
          <Pair ios={<ScreenOnboardingHi slide={1} />} android={<AndOnboarding slide={1} />} />
        </DCArtboard>
        <DCArtboard id="onb-2" label="02 · Quick Log" width={PAIR_W} height={PAIR_H} style={PAIR_ABS}>
          <Pair ios={<ScreenOnboardingHi slide={2} />} android={<AndOnboarding slide={2} />} />
        </DCArtboard>
        <DCArtboard id="onb-3" label="02 · Shared Costs" width={PAIR_W} height={PAIR_H} style={PAIR_ABS}>
          <Pair ios={<ScreenOnboardingHi slide={3} />} android={<AndOnboarding slide={3} />} />
        </DCArtboard>
        <DCArtboard id="onb-4" label="02 · Event Budget" width={PAIR_W} height={PAIR_H} style={PAIR_ABS}>
          <Pair ios={<ScreenOnboardingHi slide={4} />} android={<AndOnboarding slide={4} />} />
        </DCArtboard>
        <DCArtboard id="onb-5" label="02 · Journal · Get started" width={PAIR_W} height={PAIR_H} style={PAIR_ABS}>
          <Pair ios={<ScreenOnboardingHi slide={5} />} android={<AndOnboarding slide={5} />} />
        </DCArtboard>
      </DCSection>

      {/* ─── PROFILE SETUP ─── */}
      <DCSection id="profile-setup" title="Flow 04 — Profile Setup · iOS vs Android" subtitle="First run · account-free personalization · platform-native form patterns">
        <DCArtboard id="prof-name" label="P1 · Profile name" width={PAIR_W} height={PAIR_H} style={PAIR_ABS}>
          <Pair ios={<ProfileName />} android={<AndProfileName />} />
        </DCArtboard>
        <DCArtboard id="prof-currency" label="P2 · Home currency" width={PAIR_W} height={PAIR_H} style={PAIR_ABS}>
          <Pair ios={<ProfileCurrency />} android={<AndProfileCurrency />} />
        </DCArtboard>
        <DCArtboard id="prof-currency-sheet" label="P2 · Currency picker" width={PAIR_W} height={PAIR_H} style={PAIR_ABS}>
          <Pair ios={<ProfileCurrencySheet />} android={<AndProfileCurrencySheet />} />
        </DCArtboard>
      </DCSection>

    </DesignCanvas>
  );
}

ReactDOM.createRoot(document.getElementById('root')).render(<ComparisonApp />);
