// Canvas host — wraps each Flow as a DCArtboard, exposes a Tweaks reset.

const { useState: useS } = React;

function PrototypeCanvas() {
  const [resetCounter, setResetCounter] = useS(0);
  const reset = () => {
    setResetCounter(c => c + 1);
  };

  return (
    <>
      <TweaksPanel title="Tweaks">
        <TweakSection label="Prototype controls">
          <TweakButton label="Reset to start state" onClick={reset} />
        </TweakSection>
        <TweakSection label="About">
          <div style={{ padding: '8px 0', fontSize: 11, color: 'rgba(255,255,255,0.6)', lineHeight: 1.5 }}>
            Tap-through prototype. The numeric keypad, category chips, date/time picker, @ tag picker, and Save all work. Session state persists across refreshes — use Reset to start over.
          </div>
        </TweakSection>
      </TweaksPanel>

      <DesignCanvas accent="#c8482a" background="#1f1a14">
        <DCSection id="quick-log" title="Flow 01 — Quick Log" subtitle="Hi-fi · brand palette · tap-through prototype">
          <DCArtboard
            id="flow-01"
            label="Home → Add Expense → Save"
            width={390}
            height={844}
            style={{ background: 'transparent', borderRadius: 54, overflow: 'visible', boxShadow: 'none', border: 'none' }}
          >
            <Flow01 resetCounter={resetCounter} />
          </DCArtboard>
        </DCSection>
      </DesignCanvas>
    </>
  );
}

ReactDOM.createRoot(document.getElementById('root')).render(<PrototypeCanvas />);
